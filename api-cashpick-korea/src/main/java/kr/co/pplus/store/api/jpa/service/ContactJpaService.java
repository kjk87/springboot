package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.Contact;
import kr.co.pplus.store.api.jpa.model.ContactWithMember;
import kr.co.pplus.store.api.jpa.model.Member;
import kr.co.pplus.store.api.jpa.repository.ContactRepository;
import kr.co.pplus.store.api.jpa.repository.ContactWithMemberRepository;
import kr.co.pplus.store.api.jpa.repository.MemberRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ContactJpaService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(ContactJpaService.class);



	@Autowired
	private ContactWithMemberRepository contactWithMemberRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	MemberRepository memberRepository;


	public Page<ContactWithMember> getContactListWithMember(Long memberSeqNo, Pageable pageable){
		return contactWithMemberRepository.findAllByMemberSeqNoOrderByIsMemberDescMobileNumberAsc(memberSeqNo, pageable);
	}

	public Page<ContactWithMember> getFriendList(Long memberSeqNo, Pageable pageable){
		return contactWithMemberRepository.findAllByMemberSeqNoAndIsMember(memberSeqNo, true, pageable);
	}

	public Integer getContactMemberCount(Long memberSeqNo){
		return contactWithMemberRepository.countByMemberSeqNoAndIsMember(memberSeqNo, true);
	}

	public void delete(){

		List<Member> memberList = memberRepository.findAllByAppType("luckyball");


		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for(Member member : memberList){
					List<Contact> deleteList = new ArrayList<>();
					List<Contact> contactList = contactRepository.findAllByMemberSeqNoOrderByMobileNumberDesc(member.getSeqNo());
					for(Contact contact : contactList){
						if(contact.getMobileNumber().contains("luckyball##luckyball##")){
							deleteList.add(contact);
						}
					}
					if(deleteList.size() > 0){
						contactRepository.deleteAll(deleteList);
					}

				}
			}
		});
		thread.start();

	}

}
