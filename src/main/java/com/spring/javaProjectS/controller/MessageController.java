package com.spring.javaProjectS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MessageController {

	@RequestMapping(value="/message/{msgFlag}", method = RequestMethod.GET)
	public String msgGet(@PathVariable String msgFlag, String mid, Model model) {
		//System.out.println("mid : " + mid);
		
		if(msgFlag.equals("userDeleteOk")) {
			model.addAttribute("msg", "user가 삭제 되었습니다.");
			model.addAttribute("url", "user/userList");
		}
		else if(msgFlag.equals("userDeleteNo")) {
			model.addAttribute("msg", "user가 삭제 실패~~");
			model.addAttribute("url", "user/userList");
		}
		if(msgFlag.equals("user2DeleteOk")) {
			model.addAttribute("msg", "user2가 삭제 되었습니다.");
			model.addAttribute("url", "user2/user2List");
		}
		else if(msgFlag.equals("user2DeleteNo")) {
			model.addAttribute("msg", "user2 삭제 실패~~");
			model.addAttribute("url", "user2/user2List");
		}
		else if(msgFlag.equals("user2InputOk")) {
			model.addAttribute("msg", "회원 가입 성공!!!");
			model.addAttribute("url", "user2/user2List");
		}
		else if(msgFlag.equals("user2InputNo")) {
			model.addAttribute("msg", "회원 가입 실패~~");
			model.addAttribute("url", "user2/user2List");
		}
		else if(msgFlag.equals("user2UpdateOk")) {
			model.addAttribute("msg", "정보 수정 성공!!!");
			model.addAttribute("url", "user2/user2List");
		}
		else if(msgFlag.equals("user2UpdateNo")) {
			model.addAttribute("msg", "정보 수정 실패~~");
			model.addAttribute("url", "user2/user2List");
		}
		else if(msgFlag.equals("guestInputOk")) {
			model.addAttribute("msg", "방명록에 글이 등록되었습니다.");
			model.addAttribute("url", "guest/guestList");
		}
		else if(msgFlag.equals("guestInputNo")) {
			model.addAttribute("msg", "방명록에 글올리기 실패~~~");
			model.addAttribute("url", "guest/guestInput");
		}
		else if(msgFlag.equals("adminLoginOk")) {
			model.addAttribute("msg", "관리자 인증 성공");
			model.addAttribute("url", "guest/guestList");
		}
		else if(msgFlag.equals("adminLoginNo")) {
			model.addAttribute("msg", "관리자 인증 실패~~");
			model.addAttribute("url", "guest/adminLogin");
		}
		else if(msgFlag.equals("guestDeleteOk")) {
			model.addAttribute("msg", "방명록 삭제 성공");
			model.addAttribute("url", "guest/guestList");
		}
		else if(msgFlag.equals("guestDeleteNo")) {
			model.addAttribute("msg", "방명록 삭제 실패~~");
			model.addAttribute("url", "guest/guestList");
		}
		
		return "include/message";
	}
}