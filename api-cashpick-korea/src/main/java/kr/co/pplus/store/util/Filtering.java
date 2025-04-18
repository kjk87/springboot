package kr.co.pplus.store.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filtering {
    private static final String badWords = "10알|10팔|10발넘|10발년|10발놈|10새끼|10쎄끼|10창|10챵|10탱|10탱아|10팔년|10팔놈|10할년|18놈|18세끼|18새끼|18넘|18년|강간|같이자자|개라슥|개새끼|개새야|개색|개색기|개색끼|개샛키|개샛끼|개샤끼|개쌍넘|개쌍년|개십팔|개자식|개자지|개잡년|개잡놈|개찌질이|개후랄|개후레|개후장|겁탈|계약동거|계약애인|고공섹스|고우섹스|고패티쉬|고픈녀|고픈여|공육공|과부촌|광녀|교환부부|구녕|구멍에쑤실까|국제성인마트|굴래머|굴램|굴레머|굿섹스클럽|그년|그룹섹|그룹섹스|그지새끼|그지새키|그지좃밥|그지좆밥|근친상간|꼴갑|꼴값|꼴갚|꼴깝|꼴통|꼴려|꼴리는|꼴캅|꼽냐|나너먹을께|나먹어줘|나를싸게|나를흥분|나쁜년|나쁜뇬|나체|남녀섹시속옷|남녀자위기구|남성단련용품|남성자위기구|넣게벌려|넣고싸고|네버섹스|노팬티|노브라|노출증|노콘|누드|눈깔|다리벌려|다리벌리|다보자성인영화관|다음섹스|다이섹스|단란주점|대가리|대박성인토탈몰|대줄년|돌림빵|두두섹스|뒈져|뒈져라|등신|디져라|디진다|디질래|딜도|딥콜|따먹기|따먹는|따먹어|따먹혀줄래|딸딸이|떡걸|떡촌|떡치기|라이브스트립|라이브섹스|러브베드|러브섹시클럽|러브호텔|러시아걸|레드섹스TV|레아뒤|레즈|로리타|롤리타|룸사롱|룸살롱|룸섹스|룸쌀롱|리얼에로|립서비스|마스터베이션|매춘|모노섹스|몰래보기|몰래카메라|몰래캠코더|몰카|몸안에사정|몸캠|무료몰카|무료성인|무료성인동영상|무료성인만화|무료성인방송|무료성인싸이트|무료성인엽기|무료성인영화|무료성인정보|무료섹스|무료섹스동영상|무료섹스사이트|무료야설|무료포르노|무료포르노동영상|무삭제원판|무전망가|미국포르노|미소녀섹스가이드|미친년|미친놈|바이브레이터|박고빼고|박고싶다|박고싶퍼|박아줄게|박아줄께|박을께|박을년|밖에다쌀께|밤일|배위에싸죠|벌렁거려|벌려|벌릴여자|벙개남|병신|보지|보지물|보지걸|보지구녕|보지구멍|부랄|부럴|부부섹스|불륜|불알|빠구리|빨간마후라|빨고싶다|빨고싶어|빨고파|빨구시퍼|빨구싶나|빨구싶어|빨기|빨아|빨아도|빨아봐|빨아죠|빨아주고싶어|빨어|빨어핥어박어|빨자|빨자좃|사까시|사창가|사까치|사이버섹스|사카시|삽입|상놈|상년|상노무|새꺄|새X|색골|색광|색기|색남|색녀|색마|색수|색쉬|색스|색스코리아|색쑤|색쓰|색키|색파트너|샤앙녀|샤앙년|샤럽|샤불년|샹년|샹넘|샹놈|서양뽀르노|서양이쁜이|性|성경험|성관계|성폭행|성감대|성고민상담|성과섹스|성교제|성기구|성보조기구|성욕구|성인갤러리|성인게시판|성인극장|성인놀이문화|성인대화방|성인동영상|성인드라마|성인만화|성인사이트|성인자료실|성인게임|성인공간|성인그리고섹스|성인나라|성인누드|성인뉴스|성인대화|성인만화나라|성인만화천국|성인망가|성인몰|성인무료|성인무료동영상|성인무료사이트|성인무료영화|성인무비|성인물|성인미스랭크|성인미팅방|성인방|성인방송|성인방송국|성인방송안내|성인별곡|성인비디오|성인사이트소개|성인사진|성인상품|성인생방송|성인샵|성인서적|성인성교육스쿨|성인소녀경|성인소라가이드|성인소설|성인쇼|성인쇼핑|성인시트콤|성인싸이트|성인애니|성인애니메이션|성인야동|성인야사|성인야설|성인야캠|성인야화|성인에로무비|성인에로영화|성인엽기|성인영상|성인영화|성인와레즈|성인용|성인용품|성인용CD|성인유머|성인자료|성인잡지|성인전용관|성인전용정보|성인정보|성인채팅|성인천국|성인체위|성인카툰|성인카페|성인컨텐츠|성인클럽|성인포탈|성인플래쉬|성인플래시|성인화상|성인화상채팅|성인CD|성인IJ|성체험|성추행|성충동|성클리닉|성테크닉|성행위|세끼|섹스|섹쑤|섹걸|섹골|섹남|섹녀|섹도우즈|섹뜨|섹마|섹수|섹쉬|섹슈|섹시|섹시갤러리|섹시걸|섹시게이트|섹시나라|섹시나이트|섹시누드|섹시뉴스|섹시매거진|섹시맵|섹시무비|섹시사진|섹시샵|섹시성인용품|섹시섹스코리아|섹시스타|섹시신문|섹시씨엔엔|섹시아이제이|섹시에로닷컴|섹시엔몰|섹시엔TV|섹시연예인|섹시재팬|섹시촌|섹시코디|섹시코리아|섹시클럽|섹시클릭|섹시팅하자|섹시팬티|섹시TV|섹티쉬|섹파트너|섹하자|섹한번|섹할|섹해|스트립쇼|스트립쑈|시댕|시뎅|시바|시발넘|시발년|시발놈|시뱅|시뱜|시벌|심야TV|십8|십놈|십새|써글넘|써글년|써글놈|써글|썩을넘|썩을년|썩을놈|쎄끈|씨바|씨발|씹년|씹놈|씹벌|씹알|씹창|씹탱|씹팔|씹팔년|씹새|씹새끼|씹새키|아가리|알몸|알몸쇼|애액|야동|야설|야캠|야한동영상|야한사진|야한누드|야한만화|야한사이트|양아치|에로|에로물|에로영화|에로69|에로동영상|에로라이브|에로무비|에로뮤직비디오|에로배우|에로비디오|염병|왕보지|왕자지|유방|유두|유흥|육봉|육구자세|음경|음담패설|인터넷성인방송|일본동영상|일본망가|일본미소녀|일본뽀르노|일본성인만화|일본성인방송|자위|자지|자위기구|자위남|자위녀|자위씬|자위용품|자위행위|정력|정력강화용품|정사씬모음|정사채널|정액|젖|젖가슴|젖꼭지|젖물|젖밥|젖빠지게|젖은팬티|젖탱이|젖통|조루|조빱|조또|조진다|족까|족밥|존나|존니|존만한|졷까|졷따|졸라|죤나|죤니|죨라|죳|죶|죹|주댕이|주둥이|쥐랄|쥐럴|지랄육갑|찌찌|찐따|창녀|창남|창년|凸|체위|최음제|커닐링구스|콘돔|쿤닐링구스|크리토리스|클럽에로|클럽AV스타|클리토리스|퇴폐|특수콘돔|패티쉬|패티시|페니스|페로몬|페미돔|페티걸|페팅|펠라티오|포로노|폰색|폰섹|피임기구|피임용품|호빠|호스트바|호스트빠|화냥년|화끈남|화끈녀|화류|후까시|후장|헤로인|히로뽕|FUCK|S파트너|S하고E싶다X|S하E자X|SE엑스|SECMA|SEKMA|SⓔX|ⓢEX|⒮⒠⒳|SEXSEXY|SEXY화상채팅|SORASEX";
    public static String filter(final String input) {
        Pattern p = Pattern.compile(badWords, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(buffer, maskWord(m.group()));
        }
        m.appendTail(buffer);

        return buffer.toString();
    }
    public static String maskWord(final String str) {
        StringBuffer buf = new StringBuffer();
        int i = 0;
        for (char ch : str.toCharArray()) {

            if (i < 1) {
                buf.append(ch);
            } else {
                buf.append("*");
            }
            i++;
        }
        return buf.toString();
    }
}
