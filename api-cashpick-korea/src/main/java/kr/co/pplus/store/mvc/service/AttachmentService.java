package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Attachment;
import kr.co.pplus.store.type.model.ImageInfo;
import kr.co.pplus.store.type.model.ThumbnailOption;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.KeyGenerator;
import kr.co.pplus.store.util.S3;
import kr.co.pplus.store.util.ThumbnailOptionManager;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Transactional(transactionManager = "transactionManager", readOnly = true)
public class AttachmentService extends RootService {

	private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

	@Value("${STORE.CDN_URL}")
	private String CDN_URL;

	@Value("${STORE.CDN_PATH}")
	private String CDN_PATH;


	@Value("${STORE.TYPE}")
	private String STORE_TYPE;

	@Value("${STORE.BOUND_DEFAULTIMAGE}")
	private Long BOUND_DEFAULTIMAGE = 10L;

	
	@Autowired
	private ContactService contactSvc;

	protected interface FileWrapper {

		public String getOriginalFilename();

		public void transferTo(Attachment attachment, File dest) throws Exception;

		public long getSize();

	}

	protected class MultipartFileWrapper implements FileWrapper {

		private MultipartFile file;

		public MultipartFileWrapper(MultipartFile file) {
			super();
			this.file = file;
		}

		@Override
		public String getOriginalFilename() {
			return file.getOriginalFilename();
		}

		@Override
		public void transferTo(Attachment attachment, File dest) throws IllegalStateException, IOException {
			
			if ("gif".equalsIgnoreCase(attachment.getExtension())) {
				Thumbnails.of(file.getInputStream()).scale(1).imageType(BufferedImage.TYPE_INT_ARGB).toFile(dest);
			} else {
				Thumbnails.of(file.getInputStream()).scale(1).toFile(dest);
			}
			/*
			Integer rotate = attachment.getRotate();
			if (rotate != null && rotate == 270) {
				ImageInfo ii = new ImageInfo(dest);
				if (ii.getWidth() > ii.getHeight()) {
					Thumbnails.of(dest).scale(1).rotate(-90).toFile(dest);
				}
			}
			//			file.transferTo(dest);
			 * 
			 */
		}

		@Override
		public long getSize() {
			return file.getSize();
		}
	}

	protected class IoFileWrapper implements FileWrapper {

		private File file;

		public IoFileWrapper(File file) {
			super();
			this.file = file;
		}

		@Override
		public String getOriginalFilename() {
			return file.getName();
		}

		@Override
		public void transferTo(Attachment attachment, File dest) throws Exception {
			Files.copy(file.toPath(), dest.toPath());
		}

		@Override
		public long getSize() {
			return file.length();
		}
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Attachment insert(Attachment attachment) {
		sqlSession.insert("Attachment.insert", attachment);
		return attachment ;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void saveOnlyS3(Attachment attachment, MultipartFile file) throws Exception {
		saveFile(attachment, new MultipartFileWrapper(file));
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void upload(Attachment attachment, MultipartFile file) throws Exception {
		saveFile(attachment, new MultipartFileWrapper(file));
		logger.debug(attachment.toString());
		sqlSession.insert("Attachment.insert", attachment);

		/*if (AttachTargetType.memberProfile == attachment.getTargetType()) {
			if (attachment.getTarget() != null && attachment.getTarget() instanceof User) {
					contactSvc.updateVersion((User)attachment.getTarget());
			} else {
				User user = new User();
				user.setNo(attachment.getTargetNo());
				contactSvc.updateVersion(user);
			}
		}*/
		return;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Attachment copy(Attachment attachment) throws ResultCodeException {
		Attachment dbVal = sqlSession.selectOne("Attachment.get", attachment);
		if (dbVal == null) {
			throw new NotFoundTargetException();
		}
		//참조 카운트를 증가시킨다.
		sqlSession.update("Attachment.increaseRefCount", attachment);
		return dbVal;
	}

	private void saveFile(Attachment attachment, FileWrapper fileWrapper) throws Exception {

		long timeMillis = System.currentTimeMillis();
		Date now = DateUtil.getCurrentDate();
		String fileSubPath = getFileSubPath(now, attachment.getTargetType().toString());

		String originName = fileWrapper.getOriginalFilename();
		int index = originName.lastIndexOf('.');
		String extension = originName.substring(index + 1);
		logger.debug("originName : "+originName);

		StringBuilder sb = new StringBuilder();
		sb.append(timeMillis).append("_").append(new Random().nextInt(1000000));

		String name = sb.toString();
		String fileName = name + "." + extension;
		String filePath = CDN_PATH + fileSubPath;

		attachment.setExtension(extension);

		File tmpFile = new File("/tmp/" + KeyGenerator.generateKey() + "_" + originName)  ;
		fileWrapper.transferTo(attachment, tmpFile);
		S3.getInstance(STORE_TYPE).putS3(tmpFile, filePath + File.separator + fileName) ;
//			File saveFile = new File(filePath, fileName);
//			fileWrapper.transferTo(attachment, saveFile);

		processThumbnail(attachment, tmpFile, filePath, name, extension);

		attachment.setOriginName(originName);
		attachment.setFilePath(filePath);
		attachment.setFileName(fileName);
		attachment.setFileSize(fileWrapper.getSize());
		attachment.setId(KeyGenerator.generateKey());
		attachment.setUrl(CDN_URL + fileSubPath + File.separator + fileName);

		tmpFile.delete() ;
	}

	private void processThumbnail(Attachment attachment, File saveFile, String filePath, String name,
			String extension) throws IOException {

		ImageInfo ii = new ImageInfo(saveFile);
		List<ThumbnailOption> list = ThumbnailOptionManager.get(attachment.getTargetType(),
				ii.getWidth(), ii.getHeight());

		if (list == null)
			return;

		int i = 1;

		for (ThumbnailOption thumbnailOption : list) {
			Builder<File> builder = Thumbnails.of(saveFile);
			Position position = thumbnailOption.getPosition();
			if (position != null)
				builder = builder.crop(position);

			if ("gif".equalsIgnoreCase(extension)) {
				builder.imageType(BufferedImage.TYPE_INT_ARGB);
			}

			if ("pageBackground".equals(attachment.getTargetType()) && i == 2 &&
					thumbnailOption.getWidth() > ii.getWidth() && thumbnailOption.getHeight() > ii.getHeight()) {
				File tmpThumb = new File("/tmp" + KeyGenerator.generateKey() + "_tn"+ i + "." + extension) ;
				Files.copy(saveFile.toPath(), tmpThumb.toPath());
				String thumbKey = filePath + File.separator + name + "_tn" + i + "." + extension ;
				S3.getInstance(STORE_TYPE).putS3(tmpThumb, thumbKey) ;
				tmpThumb.delete() ;
			} else {
				File tmpThumb = new File("/tmp" + KeyGenerator.generateKey() + "_tn"+ i + "." + extension) ;
				String thumbKey = filePath + File.separator + name + "_tn" + i + "." + extension ;
				builder.size(thumbnailOption.getWidth(), thumbnailOption.getHeight())
						.toFile(tmpThumb);
				S3.getInstance(STORE_TYPE).putS3(tmpThumb, thumbKey) ;
				tmpThumb.delete() ;
			}
//			if( STORE_TYPE.equals("PROD") ) {
//				if ("pageBackground".equals(attachment.getTargetType()) && i == 2 &&
//						thumbnailOption.getWidth() > ii.getWidth() && thumbnailOption.getHeight() > ii.getHeight()) {
//					File tmpThumb = new File("/tmp" + KeyGenerator.generateKey() + "_tn"+ i + "." + extension) ;
//					Files.copy(saveFile.toPath(), tmpThumb.toPath());
//					String thumbKey = filePath + File.separator + name + "_tn" + i + "." + extension ;
//					S3.getInstance().putS3(tmpThumb, thumbKey) ;
//					tmpThumb.delete() ;
//				} else {
//					File tmpThumb = new File("/tmp" + KeyGenerator.generateKey() + "_tn"+ i + "." + extension) ;
//					String thumbKey = filePath + File.separator + name + "_tn" + i + "." + extension ;
//					builder.size(thumbnailOption.getWidth(), thumbnailOption.getHeight())
//							.toFile(tmpThumb);
//					S3.getInstance().putS3(tmpThumb, thumbKey) ;
//					tmpThumb.delete() ;
//				}
//			} else {
//				if ("pageBackground".equals(attachment.getTargetType()) && i == 2 &&
//						thumbnailOption.getWidth() > ii.getWidth() && thumbnailOption.getHeight() > ii.getHeight()) {
//					Files.copy(saveFile.toPath(), new File(filePath, name + "_tn" + i + "." + extension).toPath());
//				} else {
//					builder.size(thumbnailOption.getWidth(), thumbnailOption.getHeight())
//							.toFile(new File(filePath, name + "_tn" + i + "." + extension));
//				}
//			}
			
			i++;
		}
	}

	private String getFileSubPath(Date date, String targetType) {

		StringBuilder path = new StringBuilder(targetType);
		try {
			path.append('/').append(DateUtil.getDate("yyyy/MM/dd", date));	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String fileSubPath = path.toString();

//		if( !STORE_TYPE.equals("PROD") ) {
//			File file = new File(UPLOAD_PATH, fileSubPath);
//
//			if (file.exists() == false) {
//				file.mkdirs();
//			}
//		}

		return fileSubPath;
	}

	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void delete(Attachment attachment) throws ResultCodeException {
		//이 이하의 첨부파일은 서비스에서 정의된 파일로 삭제하면 안 된다.
		if (attachment.getNo() < Const.BOUND_ATTACHMENT)
			return;

		Attachment dbVal = sqlSession.selectOne("Attachment.get", attachment);

		if (dbVal == null) {
			throw new NotFoundTargetException();
		}
		
		//참조 카운트가 1이상인 경우에는 참조 카운트만 줄인다. 다른 곳에서도 사용되는 이미지이다.
		if (dbVal.getRefCount() > 1) {
			sqlSession.update("Attachment.decreaseRefCount", attachment);
			return;
		}
			

		String filePath = dbVal.getFilePath();
		String fileName = dbVal.getFileName();
		String url = dbVal.getUrl() ;

		List<ThumbnailOption> list = ThumbnailOptionManager.get(dbVal.getTargetType(), 0, 0);

		if (list != null && list.isEmpty() == false) {
			int index = fileName.lastIndexOf('.');
			String name = fileName.substring(0, index);
			String ext = fileName.substring(index);

			for (int i = 1; i <= list.size(); i++) {
				String fileKey = filePath + File.separator + name + "_tn" + i + ext ;
				S3.getInstance(STORE_TYPE).removeS3(fileKey);
//				if( STORE_TYPE.equals("PROD") && url.startsWith("https://cdn.") ) {
//					String fileKey = filePath + File.separator + name + "_tn" + i + ext ;
//					S3.getInstance().removeS3(fileKey);
//				} else {
//					File file = new File(filePath, name + "_tn" + i + ext);
//					file.delete();
//				}
			}
		}
		String fileKey = filePath + File.separator + fileName ;
		S3.getInstance(STORE_TYPE).removeS3(fileKey);
//		if( STORE_TYPE.equals("PROD")  && url.startsWith("https://cdn.") ) {
//			String fileKey = filePath + File.separator + fileName ;
//			S3.getInstance().removeS3(fileKey);
//		} else {
//			File file = new File(filePath, fileName);
//			file.delete();
//		}

		sqlSession.delete("Attachment.remove", dbVal);
	}

	public List<Attachment> getDefaultImageList(Attachment attachment) {
		attachment.setNo(BOUND_DEFAULTIMAGE);
		return sqlSession.selectList("Attachment.getDefaultImageList", attachment);
	}

	public Attachment getRandomImage(Attachment attachment) {
		attachment.setNo(BOUND_DEFAULTIMAGE);
		return sqlSession.selectOne("Attachment.getRandomImage", attachment);
	}

	/*
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void saveByDefaultImage(DefaultImage defaultImage, Attachment attachment)
			throws Exception {

		// 이 부분은 DB에서 조회하면 targetSeqNo가 바뀌므로 defaultImageDomain 조회 전에 해야만 한다.
		attachment.setTargetType(defaultImage.getTargetType());
		attachment.setTargetNo(defaultImage.getTargetNo());

		defaultImage = dao.getDefaultImage(defaultImage);

		if (defaultImage == null) {
			throw new NotFoundTargetException();
		}

		saveFile(attachment,
				new IoFileWrapper(new File(defaultImage.getFilePath(), defaultImage.getFileName())));
		dao.insert(attachment);
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void saveByDefaultPageImage(DefaultImage defaultImage, Attachment attachment) 
			throws Exception {
		
		// 이 부분은 DB에서 조회하면 targetNo가 바뀌므로 defaultImage 조회 전에 해야만 한다.
		attachment.setTargetType(defaultImage.getTargetType());
		attachment.setTargetNo(defaultImage.getTargetNo());
		
		defaultImage = dao.getDefaultPageImage(defaultImage);

		if (defaultImage == null) {
			throw new NotFoundTargetException();
		}
		
		saveFile(attachment,
				new IoFileWrapper(new File(defaultImage.getFilePath(), defaultImage.getFileName())));
		dao.insertDefaultPageImage(attachment);
	}*/

}
