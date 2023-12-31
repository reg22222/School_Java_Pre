package tommy.spring.web.board;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
@Controller
@SessionAttributes("board")
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	@ModelAttribute("conditionMap")
	public Map<String, String> searchConditionMap() {
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("제목", "TITLE");
		conditionMap.put("내용", "CONTENT");
		return conditionMap;
	}
	
	@RequestMapping("/insertBoard.do")
	public String insertBoard(BoardVO vo) throws IOException{
		System.out.println("글 등록 처리");
		// 1. 사용자 입력 정보 추출
		MultipartFile uploadFile = vo.getUploadFile();
		if(!uploadFile.isEmpty()) {
			String fileName = uploadFile.getOriginalFilename();
			uploadFile.transferTo(new File("C:/myProject/" + fileName));
		}
		// 2. DB 처리
		boardService.insertBoard(vo);
		// 3. 화면 네비게이션
		return "getBoardList.do";
	}

	@RequestMapping("/updateBoard.do")
	public String updateBoard(@ModelAttribute("board") BoardVO vo) {
		System.out.println("글 수정 처리");
		// 1. 사용자 입력 정보 추출
		// 2. DB 연동
		boardService.updateBoard(vo);
		// 3. 화면 네비게이션
		return "getBoardList.do";
	}

	@RequestMapping("/deleteBoard.do")
	public String deleteBoard(BoardVO vo) {
		System.out.println("글 삭제 처리");
		// 1. 사용자 입력 정보 추출
		// 2. DB 연동
		boardService.deleteBoard(vo);
		// 3. 화면 네비게이션
		return "getBoardList.do";
	}

	@RequestMapping("/getBoard.do")
	public String getBoard(BoardVO vo, Model model) {
		System.out.println("글 상세 보기 처리");
		// 1. 검색할 게시글 번호 추출
		// 2. DB 연동
		BoardVO board = boardService.getBoard(vo);
		// 3. 응답 화면 구현
		model.addAttribute("board", board);
		return "getBoard.jsp";
	}

	@RequestMapping("/getBoardList.do")
	public String getBoardList(BoardVO vo, Model model) {
		System.out.println("글 목록 검색 처리");
		// 1. 사용자 입력 정보 추출
		if(vo.getSearchCondition() == null) vo.setSearchCondition("TITLE");
		if(vo.getSearchKeyword() == null) vo.setSearchKeyword("");
		// 2. DB 연동
		List<BoardVO> boardList = boardService.getBoardList(vo);
		// 3. 응답 화면 구성
		model.addAttribute("boardList", boardList); // Model 정보저장
		return "getBoardList.jsp"; // View 정보저장
	}
	
	@RequestMapping("/dataTransformJSON.do")
	@ResponseBody
	public List<BoardVO> dataTransformJSON(BoardVO vo){
		vo.setSearchCondition("TITLE");
		vo.setSearchKeyword("");
		List<BoardVO> boardList = boardService.getBoardList(vo);
		return boardList;
	}
	
	@RequestMapping("/dataTransformXML.do")
	@ResponseBody
	public BoardListVO dataTransformXML(BoardVO vo){
		vo.setSearchCondition("TITLE");
		vo.setSearchKeyword("");
		List<BoardVO> boardList = boardService.getBoardList(vo);
		BoardListVO boardListVO = new BoardListVO();
		boardListVO.setBoardList(boardList);
		return boardListVO;
	}
}
