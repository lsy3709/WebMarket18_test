package mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.model.BoardDAO;
import mvc.model.BoardDTO;

public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 해다 게시판의 페이징 처리하기위한 상수값, -> 목록에 보여주는 갯수.
	static final int LISTCOUNT = 5; 

	//get 로 전송되어도, post 방식으로 다 처리하는 로직으로 예제 구성이 되어있음.
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
//보드 관련된 모든 처리를 다하는 로직이라서,
	// 게시판에 접속만 하더라도, 콘솔 상에서 확인 가능함. 
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String RequestURI = request.getRequestURI();
		System.out.println("RequestURI의 값 : " +RequestURI);
		String contextPath = request.getContextPath();
		System.out.println("contextPath의 값 : " +contextPath);
		String command = RequestURI.substring(contextPath.length());
		System.out.println("command의 값 : " +command);
		
		response.setContentType("text/html; charset=utf-8");
		request.setCharacterEncoding("utf-8");
	
		if (command.equals("/BoardListAction.do")) {//��ϵ� �� ��� ������ ����ϱ�
			// 게시판의 페이지 정보랑, 게시물 정보등을 불러와서, 
			// 해당 request 객체에 담아두는역할. 
			requestBoardList(request);
			
			RequestDispatcher rd = request.getRequestDispatcher("./board/list.jsp");
			rd.forward(request, response);
		} else if (command.equals("/BoardWriteForm.do")) { // �� ��� ������ ����ϱ�
				requestLoginName(request);
				RequestDispatcher rd = request.getRequestDispatcher("./board/writeForm.jsp");
				rd.forward(request, response);				
		} else if (command.equals("/BoardWriteAction.do")) {// ���ο� �� ����ϱ�
				requestBoardWrite(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);						
		} else if (command.equals("/BoardViewAction.do")) {//���õ� �� �� ������ ��������
				requestBoardView(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardView.do");
				rd.forward(request, response);						
		} else if (command.equals("/BoardView.do")) { //�� �� ������ ����ϱ�
				RequestDispatcher rd = request.getRequestDispatcher("./board/view.jsp");
				rd.forward(request, response);	
		} else if (command.equals("/BoardUpdateAction.do")) { //���õ� ���� ��ȸ�� �����ϱ�
				requestBoardUpdate(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);
		}else if (command.equals("/BoardDeleteAction.do")) { //���õ� �� �����ϱ�
				requestBoardDelete(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);				
		} 
	}
	//��ϵ� �� ��� ��������	
	public void requestBoardList(HttpServletRequest request){

		// 임시로 , 게시판 목록 화면에 출력하기 위해서, 해당 정보들의 변수를 선언 및 재할당.
		// 만약. 이 정보를 계속 사용하겠다고 하면, 위에서 전역 또는 선언만 하고
		// 재할당해서 이용 가능. 
		String RequestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = RequestURI.substring(contextPath.length());
		
		
		BoardDAO dao = BoardDAO.getInstance();
		List<BoardDTO> boardlist = new ArrayList<BoardDTO>();
		
	  	int pageNum=1;
		int limit=LISTCOUNT;
		
		if(request.getParameter("pageNum")!=null)
			pageNum=Integer.parseInt(request.getParameter("pageNum"));
				
		String items = request.getParameter("items");
		String text = request.getParameter("text");
		
		int total_record=dao.getListCount(items, text);
		boardlist = dao.getBoardList(pageNum,limit, items, text); 
		
		int total_page;
		
		if (total_record % limit == 0){     
	     	total_page =total_record/limit;
	     	Math.floor(total_page);  
		}
		else{
		   total_page =total_record/limit;
		   Math.floor(total_page); 
		   total_page =  total_page + 1; 
		}		
		//해당 RequestURI, contextPath, command 를 해당 뷰에 데이터를 전달함.
		// 해당 뷰에서, 키이름으로 해당 값을 불러와서 사용할 예정. 
		request.setAttribute("RequestURI의 값", RequestURI);
		request.setAttribute("contextPath의 값", contextPath);
		request.setAttribute("command의 값", command);
		
   		request.setAttribute("pageNum", pageNum);		  
   		request.setAttribute("total_page", total_page);   
		request.setAttribute("total_record",total_record); 
		request.setAttribute("boardlist", boardlist);								
	}
	//������ ����ڸ� ��������
	public void requestLoginName(HttpServletRequest request){
					
		String id = request.getParameter("id");
		
		BoardDAO  dao = BoardDAO.getInstance();
		
		String name = dao.getLoginNameById(id);		
		
		request.setAttribute("name", name);									
	}
	// ���ο� �� ����ϱ�
	public void requestBoardWrite(HttpServletRequest request){
					
		BoardDAO dao = BoardDAO.getInstance();		
		
		BoardDTO board = new BoardDTO();
		board.setId(request.getParameter("id"));
		board.setName(request.getParameter("name"));
		board.setSubject(request.getParameter("subject"));
		board.setContent(request.getParameter("content"));	
		
		System.out.println(request.getParameter("name"));
		System.out.println(request.getParameter("subject"));
		System.out.println(request.getParameter("content"));
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
		String regist_day = formatter.format(new java.util.Date()); 
		
		board.setHit(0);
		board.setRegist_day(regist_day);
		board.setIp(request.getRemoteAddr());			
		
		dao.insertBoard(board);								
	}
	//���õ� �� �� ������ ��������
	public void requestBoardView(HttpServletRequest request){
					
		BoardDAO dao = BoardDAO.getInstance();
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDTO board = new BoardDTO();
		board = dao.getBoardByNum(num, pageNum);		
		
		request.setAttribute("num", num);		 
   		request.setAttribute("page", pageNum); 
   		request.setAttribute("board", board);   									
	}
	//���õ� �� ���� �����ϱ�
	public void requestBoardUpdate(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();		
		
		BoardDTO board = new BoardDTO();		
		board.setNum(num);
		board.setName(request.getParameter("name"));
		board.setSubject(request.getParameter("subject"));
		board.setContent(request.getParameter("content"));		
		
		 java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd(HH:mm:ss)");
		 String regist_day = formatter.format(new java.util.Date()); 
		 
		 board.setHit(0);
		 board.setRegist_day(regist_day);
		 board.setIp(request.getRemoteAddr());			
		
		 dao.updateBoard(board);								
	}
	//���õ� �� �����ϱ�
	public void requestBoardDelete(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();
		dao.deleteBoard(num);							
	}	
}
