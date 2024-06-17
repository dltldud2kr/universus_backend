package com.example.gazamung.admin;

import com.example.gazamung.announcement.Announcement;
import com.example.gazamung.announcement.AnnouncementCreateReq;
import com.example.gazamung.announcement.AnnouncementRepository;
import com.example.gazamung.announcement.AnnouncementService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementService announcementService;

    @Autowired
    public AdminController(MemberRepository memberRepository, UniversityRepository universityRepository,
                           DepartmentRepository departmentRepository, UnivBoardRepository univBoardRepository,
                           ReplyRepository replyRepository, AnnouncementRepository announcementRepository,
                           AnnouncementService announcementService) {
        this.memberRepository = memberRepository;
        this.universityRepository = universityRepository;
        this.departmentRepository = departmentRepository;
        this.univBoardRepository = univBoardRepository;
        this.replyRepository = replyRepository;
        this.announcementRepository = announcementRepository;
        this.announcementService = announcementService;
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

    @GetMapping("/announcement")
    public String showAnnouncementList(Model model) {
        List<Announcement> announcements = announcementRepository.findAll();
        model.addAttribute("announcements", announcements);
        return "announcement";
    }

    @GetMapping("/announcement/create")
    public String showCreateForm(Model model) {
        model.addAttribute("announcementCreateReq", new AnnouncementCreateReq());
        return "createAnnouncement";
    }

    @PostMapping("/announcement/create")
    public String createAnnouncement(@ModelAttribute AnnouncementCreateReq request, RedirectAttributes attributes) {
        announcementService.create(request);
        attributes.addAttribute("showAnnouncementList", true); // 파라미터 추가
        return "redirect:/api/v1/admin/main";
    }

    @PostMapping("/announcement/delete/{idx}")
    public String deleteAnnouncement(@PathVariable Long idx, RedirectAttributes attributes) {
        announcementService.delete(idx);
        attributes.addAttribute("showAnnouncementList", true); // 파라미터 추가
        return "redirect:/api/v1/admin/main";
    }

    @GetMapping("/announcement/info/{idx}")
    public String infoAnnouncement(@PathVariable Long idx, Model model) {
        Announcement announcement = announcementService.read(idx);
        model.addAttribute("announcement", announcement);
        return "infoAnnouncement"; // 공지사항 정보 템플릿 반환
    }
}
