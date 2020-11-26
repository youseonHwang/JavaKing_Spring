package com.bit.university.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bit.university.dao.BoardDao;

@Controller
public class ListBoardController {

	public static int board_count = 0;
	public static int total_page = 1;


	@Autowired
	private BoardDao b_dao;

	@GetMapping("/login/listBoard.do") 
	public void listBoardView(HttpServletRequest request, Model model) {
		
		int board_boardno = 100;
		String board_category = "공지사항";
		
		if((request.getParameter("board_boardno"))!=null) { //다른 게시판을 선택한 경우
			board_boardno = Integer.parseInt(request.getParameter("board_boardno"));
			switch (board_boardno) {
				case 100: board_category = "공지사항"; break;
				case 200: board_category = "시설QNA"; break;
				case 300: board_category = "자유게시판"; break;
			}	
		}
		
		if((request.getParameter("board_category"))!=null && !(request.getParameter("board_category").equals(""))) {
			board_category = (String) request.getParameter("board_category");
		}
		
		
		System.out.println(board_category+"////"+ board_boardno);
		
		model.addAttribute("board_category", board_category);
		model.addAttribute("board_boardno", board_boardno);
		
		
	}


	@GetMapping("/login/listBoard")
	@ResponseBody
	public HashMap listBoardGet(HttpServletRequest request, HttpSession session, 
			int page_num, int page_size, String keyword, String search,
			int board_boardno,String board_category) throws Throwable {

		session = request.getSession();

		//ajax을 통해 받아온 page_num과 page_size확인
		System.out.println("page_num는"+page_num);
		System.out.println("page_size는"+page_size);
		System.out.println("keyword는"+keyword);
		System.out.println("search는"+search);
		//-----------------------------------------------------------------------------------------------------------------------------

		//search(검색어가) null이 아닌 경우 검색 요청이 있는거니깐 keyword와 search를 session에 상태유지
		if(search != null) {
			session.setAttribute("search", search);
			session.setAttribute("keyword", keyword);
		}
		//상태 유지가 되어있는 검색어가 있으면 해당하는 값들을 null로 온 search와 keyword에 넣어줌
		if(session.getAttribute("search")!=null) {
			search = (String)session.getAttribute("search");
			keyword = (String)session.getAttribute("keyword"); 
		}
		System.out.println("여기까지요44444444444444");


		//--------------------------------------------------------------------------------------

		System.out.println("board_category는 "+ board_category);
		//board_count변수에 해당 게시판의 모든 게시물 수를 반환한 값을 저장
		board_count=b_dao.getBoardCount(board_boardno, board_category, search, keyword);

		int start = (page_num-1)*page_size+1;
		int end = start+page_size-1;
		if(end > board_count) {
			end = board_count;
		}   

		
		//------------------------------------------------------------------------------------
	

		//table에 넣을 게시물 list를 위한 map변수
		HashMap for_list_map = new HashMap();

		for_list_map.put("board_boardno", board_boardno);
		for_list_map.put("board_category", board_category);
		for_list_map.put("page_num", page_num);
		for_list_map.put("search", search);
		for_list_map.put("keyword", keyword);
		for_list_map.put("start", start);
		for_list_map.put("end", end);
		//-------------------------------------------------------------------------------------------

		HashMap all_map =new HashMap();
		
		String board_boardname="";
		
		switch (board_boardno) {
		case 100: board_boardname = "알림마당"; break;
		case 200: board_boardname = "도움마당"; break;
		case 300: board_boardname = "참여마당"; break;
		}
		
		all_map.put("board_boardname", board_boardname);
		all_map.put("board_count", board_count);
		all_map.put("board_boardno", board_boardno);
		all_map.put("board_category", board_category);
		all_map.put("page_num", page_num);
		all_map.put("search", search);
		all_map.put("keyword", keyword);
		all_map.put("start", start);
		all_map.put("end", end);
		all_map.put("category_list", b_dao.getBoardCategory(board_boardno));
		all_map.put("list", b_dao.listAll(for_list_map));



		return all_map;


	}

}