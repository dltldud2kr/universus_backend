package com.example.gazamung.admin;

import com.example.gazamung.department.repository.DepartmentRepository;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.reply.entity.Reply;
import com.example.gazamung.reply.repository.ReplyRepository;
import com.example.gazamung.univBoard.entity.UnivBoard;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import com.example.gazamung.university.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final DepartmentRepository departmentRepository;
    private final UnivBoardRepository univBoardRepository;
    private final ReplyRepository replyRepository;

    @Autowired
    public AdminController(MemberRepository memberRepository, UniversityRepository universityRepository,
                           DepartmentRepository departmentRepository, UnivBoardRepository univBoardRepository,
                           ReplyRepository replyRepository) {
        this.memberRepository = memberRepository;
        this.universityRepository = universityRepository;
        this.departmentRepository = departmentRepository;
        this.univBoardRepository = univBoardRepository;
        this.replyRepository = replyRepository;
    }

    @GetMapping("/main")
    public String showAdminPage() {
        return "main"; // 관리자 메인 페이지 템플릿 반환
    }

    @GetMapping("/member")
    public String showMemberList(Model model) {
        List<Member> members = memberRepository.findAll(); // 회원 목록 조회
        Map<Long, String> universityNames = new HashMap<>();
        Map<Long, String> departmentNames = new HashMap<>();

        for (Member member : members) {
            // 대학명 조회 및 저장
            universityRepository.findById(member.getUnivId()).ifPresent(univ -> universityNames.put(member.getUnivId(), univ.getSchoolName()));
            // 학과명 조회 및 저장
            departmentRepository.findById(member.getDeptId()).ifPresent(dept -> departmentNames.put(member.getDeptId(), dept.getDeptName()));
        }

        model.addAttribute("members", members);
        model.addAttribute("universityNames", universityNames);
        model.addAttribute("departmentNames", departmentNames);

        return "member"; // 회원 목록 템플릿 반환
    }

    @GetMapping("/univBoard")
    public String showUnivBoardList(Model model){
        List<UnivBoard> univBoards = univBoardRepository.findAll();
        Map<Long, String> universityNames = new HashMap<>();
        Map<Long, String> departmentNames = new HashMap<>();

        for (UnivBoard univBoard : univBoards) {
            // 대학명 조회 및 저장
            universityRepository.findById(univBoard.getUnivId()).ifPresent(univ -> universityNames.put(univBoard.getUnivId(), univ.getSchoolName()));
            // 학과명 조회 및 저장
            departmentRepository.findById(univBoard.getDeptId()).ifPresent(dept -> departmentNames.put(univBoard.getDeptId(), dept.getDeptName()));
        }
        model.addAttribute("univBoards", univBoards);
        model.addAttribute("universityNames", universityNames);
        model.addAttribute("departmentNames", departmentNames);

        return "univBoard";
    }

    @GetMapping("/reply")
    public String showReplyList(Model model){
        List<Reply> replies = replyRepository.findAll();

        model.addAttribute("replies", replies);

        return "reply";
    }


}
