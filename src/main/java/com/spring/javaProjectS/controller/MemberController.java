package com.spring.javaProjectS.controller;

import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import com.spring.javaProjectS.service.MemberService;
import com.spring.javaProjectS.vo.MailVO;
import com.spring.javaProjectS.vo.MemberVO;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	JavaMailSender mailSender;
	
	// 회원 로그인폼 보여주기
	@RequestMapping(value = "/memberLogin", method = RequestMethod.GET)
	public String memberLoginGet(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if(cookies != null) {
			for(int i=0; i<cookies.length; i++) {
				if(cookies[i].getName().equals("cMid")) {
					request.setAttribute("mid", cookies[i].getValue());
					break;
				}
			}
		}
		
		return "member/memberLogin";
	}
	
	// 회원 로그인 체크
	@RequestMapping(value = "/memberLogin", method = RequestMethod.POST)
	public String memberLoginPost(HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="mid", defaultValue = "hkd1234", required=false) String mid,
			@RequestParam(name="pwd", defaultValue = "1234", required=false) String pwd,
			@RequestParam(name="idSave", defaultValue = "", required=false) String idSave) {
		MemberVO vo = memberService.getMemberIdCheck(mid);
		
		if(vo != null && vo.getUserDel().equals("NO") && passwordEncoder.matches(pwd, vo.getPwd())) {
			
			// 1.세션처리
			String strLevel = "";
			if(vo.getLevel() == 0) strLevel = "관리자";
			else if(vo.getLevel() == 1) strLevel = "우수회원";
			else if(vo.getLevel() == 2) strLevel = "정회원";
			else if(vo.getLevel() == 3) strLevel = "준회원";
			
//			HttpSession session = request.getSession();
			session.setAttribute("sMid", mid);
			session.setAttribute("sNickName", vo.getNickName());
			session.setAttribute("sLevel", vo.getLevel());
			session.setAttribute("strLevel", strLevel);
			
			
			// 2.쿠키저장/삭제
//			String idSave = request.getParameter("idSave")==null ? "off" : "on";
			Cookie cookieMid = new Cookie("cMid", mid);
//			cookieMid.setPath("/");
			if(idSave.equals("on")) {
				cookieMid.setMaxAge(60*60*24*7);
				response.addCookie(cookieMid);
			}
			else {
				Cookie[] cookies = request.getCookies();
				for(int i=0; i<cookies.length; i++) {
					if(cookies[i].getName().equals("cMid")) {
						cookies[i].setMaxAge(0);
						response.addCookie(cookies[i]);
						break;
					}
				}
			}
			
			return "redirect:/message/memberLoginOk?mid="+mid;
		}
		else {
			return "redirect:/message/memberLoginNo";
		}
	}
	
	// 회원 로그아웃 처리
	@RequestMapping(value = "/memberLogout", method = RequestMethod.GET)
	public String memberLogoutGet(HttpSession session) {
		String mid = (String) session.getAttribute("sMid");
		session.invalidate();
		
		return "redirect:/message/memberLogout?mid="+mid;
	}
	
	@RequestMapping(value="/memberMain", method = RequestMethod.GET)
	public String memberMainGet() {
		return "member/memberMain";
	}
	
	@RequestMapping(value="/memberJoin", method = RequestMethod.GET)
	public String memberMainJoinGet() {
		return "member/memberJoin";
	}
	
	@RequestMapping(value="/memberJoin", method = RequestMethod.POST)
	public String memberJoinPost(MemberVO vo) {
		// 아이디 / 닉네임 중복체크
		if (memberService.getMemberIdCheck(vo.getMid()) != null) return "redirect:/message/idCheckNo";
		if (memberService.getMemberNickCheck(vo.getNickName()) != null) return "redirect:/message/nickCheckNo";
		
		// 비밀번호 암호화
		vo.setPwd(passwordEncoder.encode(vo.getPwd()));
		
		// 회원사진 처리(service객체에서 처리 후 DB에 저장한다....)
		int res = memberService.setMemberJoinOk(vo);
		
		if(res == 1) return "redirect:/message/memberJoinOk";
		else return "redirect:/message/memberJoinNo";
		
		}
	
	@ResponseBody
	@RequestMapping(value="/memberIdCheck", method = RequestMethod.POST)
	public String memberMainPost(String mid) {
		MemberVO vo = memberService.getMemberIdCheck(mid);
		
		if(vo != null) return "1";
		else return "0";
	}
	
	@ResponseBody
	@RequestMapping(value="/memberNickCheck", method = RequestMethod.POST)
	public String memberNickCheckPost(String nickName) {
		MemberVO vo = memberService.getMemberNickCheck(nickName);
		
		if(vo != null) return "1";
		else return "0";
	}
	
	@RequestMapping(value="/memberDeleteUpdate", method = RequestMethod.GET)
	public String memberDeleteUpdateGet(HttpSession session) {
		String mid = (String) session.getAttribute("sMid");
		
		int res = memberService.setMemberDeleteUpdate(mid);
		
		if (res == 1) return "redirect:/message/memberDeleteOk";
		else return "redirect:/message/memberDeleteNo";
	}
	
	@RequestMapping(value="/memberPwdUpdate", method = RequestMethod.GET)
	public String MemberPwdUpdateGet() {
		return "member/memberPwdUpdate";
	}
	
	@ResponseBody
	@RequestMapping(value="/memberPwdUpdate", method = RequestMethod.POST)
	public String MemberPwdUpdatePost(HttpSession session, String pwd) {
		
		String mid = (String) session.getAttribute("sMid");
		MemberVO vo = memberService.getMemberIdCheck(mid);
				
		String res = "0"; 
		if(passwordEncoder.matches(pwd,vo.getPwd())) res = "1";
		
		
		return res;
	}
	
	// 비밀번호 찾기
	@ResponseBody
	@RequestMapping(value = "/memberPasswordSearch", method = RequestMethod.POST)
	public String memberPasswordSearchPost(String mid, String email) throws MessagingException {
		MemberVO vo = memberService.getMemberIdCheck(mid);
		if(vo != null && vo.getEmail().equals(email)) {
			// 정보 확인 후, 임시 비밀번호를 발급받아서 메일로 전송처리 한다.
			UUID uid = UUID.randomUUID();
			String pwd = uid.toString().substring(0,8);
			
			// 발급받은 비밀번호를 암호화 후 DB에 저장한다.
			memberService.setMemberPasswordUpdate(mid, passwordEncoder.encode(pwd));
			
			// 발급받은 임시번호를 회원 메일주소로 전송처리한다.
			String title = "임시 비밀번호를 발급하였습니다.";
			String mailFlag= "임시 비밀번호 : " + pwd;
			String res = mailSend(email, title, mailFlag);
			
			if(res == "1") return "1";
		}

		return "0";
	}
	
	// 메일 전송하기
	public String mailSend(String toMail, String title, String mailFlag) throws MessagingException {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String content = "";
		// 메일 전송을 위한 객체 : MimeMessage(), MimeMessageHelper()
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
		
		// 메일보관함에 회원이 보내온 메세지들의 정보를 모두 저장시킨 후 작업처리하자...
		messageHelper.setTo(toMail);
		messageHelper.setSubject(title);
		messageHelper.setText(content);
		
		// 메세지 보관함의 내용(content)에 발신자의 필요한 정보를 추가로 담아서 전송시켜주면 좋다....
		content = content.replace("\n", "<br>");
		content += "<br><hr><h3>"+mailFlag+"</h3><hr><br>";
		content += "<p><img src=\"cid:main.png\" width='500px'></p>";
		content += "<p>방문하기 : <a href='49.142.157.251:9090/cjgreen'>JavaProject</a></p>";
		content += "<hr>";
		messageHelper.setText(content, true);
		
		// 본문에 기재된 그림파일의 경로와 파일명을 별로도 표시한다. 그런후 다시 보관함에 저장한다.
		FileSystemResource file = new FileSystemResource(request.getSession().getServletContext().getRealPath("/resources/images/main.png"));
		// FileSystemResource file = new FileSystemResource("D:\\JavaProject\\springframework\\works\\javaProjectS\\src\\main\\webapp\\resources\\images\\main.png");
		messageHelper.addInline("main.png", file);
		
		// 메일 전송하기
		mailSender.send(message);
		
		return "1";
	}
	
}
