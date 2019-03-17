package com.autogen.controller.admin;

import com.autogen.domain.Paper;
import com.autogen.domain.Question;
import com.autogen.service.PaperService;
import com.autogen.service.QuestionService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author hiram 2019年03月17日 02:25
 */
@Controller
public class PaperController {

    @Autowired
    private PaperService paperService;
    @Autowired
    private QuestionService questionService;

    @RequestMapping("/papersPage")
    public String toQuestionPage(Model model) {
        List<Paper> papers = paperService.getAllPapers();
        model.addAttribute("papers", papers);
        System.out.println(papers);
        return "admin/papers";
    }

    @RequestMapping("/addPaper")
    public String toAddPaper(Model model) {
        List<Question> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "admin/paperAdd";
    }

    @RequestMapping("/addPaperAuto")
    public String addPaperAuto(String radioLevel, String multipleLevel, String judgeLevel, String fillingLevel, String essayLevel, Model model, HttpServletRequest request) {
        String chapterSectionsStr = request.getParameter("chapter");
        String title = request.getParameter("title");
        if (title == null || title.length() <= 0) {
            model.addAttribute("error", "标题不能为空");
            return "admin/paperAdd";
        }
        int radioNum = Integer.parseInt(request.getParameter("radioNum"));
        int multipleNum = Integer.parseInt(request.getParameter("multipleNum"));
        int judgeNum = Integer.parseInt(request.getParameter("judgeNum"));
        int fillingNum = Integer.parseInt(request.getParameter("fillingNum"));
        int essayNum = Integer.parseInt(request.getParameter("essayNum"));
        List<Question> radioQuestionsList = new ArrayList<>();
        List<Question> multipleQuestionsList = new ArrayList<>();
        List<Question> judgeQuestionsList = new ArrayList<>();
        List<Question> fillingQuestionsList = new ArrayList<>();
        List<Question> essayQuestionsList = new ArrayList<>();
        if (chapterSectionsStr == null || chapterSectionsStr.equals("") || chapterSectionsStr.equals("all")) {
            radioQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(null, null, Arrays.asList(radioLevel.split(",")), 1));
            multipleQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(null, null, Arrays.asList(multipleLevel.split(",")), 2));
            judgeQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(null, null, Arrays.asList(judgeLevel.split(",")), 3));
            fillingQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(null, null, Arrays.asList(fillingLevel.split(",")), 4));
            essayQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(null, null, Arrays.asList(essayLevel.split(",")), 5));
        } else {
            String[] chapterSections = chapterSectionsStr.split(",");
            for (String chapterSection : chapterSections) {
                if (chapterSection.split("-").length > 1) {
                    String chapter = chapterSection.split("-")[0];
                    String section = chapterSection.split("-")[1];
                    System.out.println(chapter + " " + section + " " + radioLevel + " " + 1);
                    radioQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, section, Arrays.asList(radioLevel.split(",")), 1));
                    multipleQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, section, Arrays.asList(multipleLevel.split(",")), 2));
                    judgeQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, section, Arrays.asList(judgeLevel.split(",")), 3));
                    fillingQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, section, Arrays.asList(fillingLevel.split(",")), 4));
                    essayQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, section, Arrays.asList(essayLevel.split(",")), 5));
                } else {
                    String chapter = chapterSection.split("-")[0];
                    radioQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, null, Arrays.asList(radioLevel.split(",")), 1));
                    multipleQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, null, Arrays.asList(multipleLevel.split(",")), 2));
                    judgeQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, null, Arrays.asList(judgeLevel.split(",")), 3));
                    fillingQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, null, Arrays.asList(fillingLevel.split(",")), 4));
                    essayQuestionsList.addAll(questionService.getQuestionsByChapterSectionLevelType(chapter, null, Arrays.asList(essayLevel.split(",")), 5));
                }
            }
        }
        Paper paper = new Paper();
        paper.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        paper.setTitle(title);
        paper.setTime(request.getParameter("time"));
        paper.setLevel(Integer.parseInt(request.getParameter("paperLevel")));
        List<Question> radioQuestions = new ArrayList<>();
        List<Question> multipleQuestions = new ArrayList<>();
        List<Question> judgeQuestions = new ArrayList<>();
        List<Question> fillingQuestions = new ArrayList<>();
        List<Question> essayQuestions = new ArrayList<>();
        // 根据数量随机抽取
        if (radioQuestionsList.size() < radioNum) {
            model.addAttribute("error", "题库【单选题】数量不足");
            return "admin/paperAdd";
        } else if (radioQuestionsList.size() == radioNum){
            StringBuilder radioIds = new StringBuilder();
            radioQuestionsList.forEach((question) -> radioIds.append(question.getId()).append(","));
            paper.setRadioIds(radioIds.length() > 0 ? radioIds.toString().substring(0, radioIds.length() - 1) : null);
            model.addAttribute("radioQuestions", radioQuestionsList);
        } else {
            makeRandom(0, radioQuestionsList.size(), radioNum).forEach((index) -> radioQuestions.add(radioQuestionsList.get(index)));
            StringBuilder radioIds = new StringBuilder();
            radioQuestions.forEach((question) -> radioIds.append(question.getId()).append(","));
            paper.setRadioIds(radioIds.length() > 0 ? radioIds.toString().substring(0, radioIds.length() - 1) : null);
            model.addAttribute("radioQuestions", radioQuestions);
        }
        paper.setRadioNum(radioNum);
        if (multipleQuestionsList.size() < multipleNum) {
            model.addAttribute("error", "题库【多选题】数量不足");
            return "admin/paperAdd";
        } else if (multipleQuestionsList.size() == multipleNum){
            StringBuilder multipleIds = new StringBuilder();
            multipleQuestionsList.forEach((question) -> multipleIds.append(question.getId()).append(","));
            paper.setMultipleIds(multipleIds.length() > 0 ? multipleIds.toString().substring(0, multipleIds.length() - 1) : null);
            model.addAttribute("multipleQuestions", multipleQuestionsList);
        } else {
            makeRandom(0, multipleQuestionsList.size(), multipleNum).forEach((index) -> multipleQuestions.add(multipleQuestionsList.get(index)));
            StringBuilder multipleIds = new StringBuilder();
            multipleQuestions.forEach((question) -> multipleIds.append(question.getId()).append(","));
            paper.setRadioIds(multipleIds.length() > 0 ? multipleIds.toString().substring(0, multipleIds.length() - 1) : null);
            model.addAttribute("multipleQuestions", multipleQuestions);
        }
        paper.setMultipleNum(multipleNum);
        if (judgeQuestionsList.size() < judgeNum) {
            model.addAttribute("error", "题库【判断题】数量不足");
            return "admin/paperAdd";
        } else if (judgeQuestionsList.size() == judgeNum){
            StringBuilder judgeIds = new StringBuilder();
            judgeQuestionsList.forEach((question) -> judgeIds.append(question.getId()).append(","));
            paper.setJudgeIds(judgeIds.length() > 0 ? judgeIds.toString().substring(0, judgeIds.length() - 1) : null);
            model.addAttribute("judgeQuestions", judgeQuestionsList);
        } else {
            makeRandom(0, judgeQuestionsList.size(), judgeNum).forEach((index) -> judgeQuestions.add(judgeQuestionsList.get(index)));
            StringBuilder judgeIds = new StringBuilder();
            judgeQuestions.forEach((question) -> judgeIds.append(question.getId()).append(","));
            paper.setJudgeIds(judgeIds.length() > 0 ? judgeIds.toString().substring(0, judgeIds.length() - 1) : null);
            model.addAttribute("judgeQuestions", judgeQuestions);
        }
        paper.setJudgeNum(judgeNum);
        if (fillingQuestionsList.size() < fillingNum) {
            model.addAttribute("error", "题库【填空题】数量不足");
            return "admin/paperAdd";
        } else if (fillingQuestionsList.size() == fillingNum){
            StringBuilder fillingIds = new StringBuilder();
            fillingQuestionsList.forEach((question) -> fillingIds.append(question.getId()).append(","));
            paper.setFillingIds(fillingIds.length() > 0 ? fillingIds.toString().substring(0, fillingIds.length() - 1) : null);
            model.addAttribute("fillingQuestions", fillingQuestionsList);
        } else {
            makeRandom(0, fillingQuestionsList.size(), fillingNum).forEach((index) -> fillingQuestions.add(fillingQuestionsList.get(index)));
            StringBuilder fillingIds = new StringBuilder();
            fillingQuestions.forEach((question) -> fillingIds.append(question.getId()).append(","));
            paper.setFillingIds(fillingIds.length() > 0 ? fillingIds.toString().substring(0, fillingIds.length() - 1) : null);
            model.addAttribute("fillingQuestions", fillingQuestions);
        }
        paper.setFillingNum(fillingNum);
        if (essayQuestionsList.size() < essayNum) {
            model.addAttribute("error", "题库【问答题】数量不足");
            return "admin/paperAdd";
        } else if (essayQuestionsList.size() == essayNum){
            StringBuilder essayIds = new StringBuilder();
            essayQuestionsList.forEach((question) -> essayIds.append(question.getId()).append(","));
            paper.setEssayIds(essayIds.length() > 0 ? essayIds.toString().substring(0, essayIds.length() - 1) : null);
            model.addAttribute("essayQuestions", essayQuestionsList);
        } else {
            makeRandom(0, essayQuestionsList.size(), essayNum).forEach((index) -> essayQuestions.add(essayQuestionsList.get(index)));
            StringBuilder essayIds = new StringBuilder();
            essayQuestions.forEach((question) -> essayIds.append(question.getId()).append(","));
            paper.setEssayIds(essayIds.length() > 0 ? essayIds.toString().substring(0, essayIds.length() - 1) : null);
            model.addAttribute("essayQuestions", essayQuestions);
        }
        paper.setEssayNum(essayNum);
        paper.setState("1");
        paper.setCreateTime(new Date());
        System.out.println(paper);
        model.addAttribute("paper", paper);
        paperService.addPaper(paper);
        return "admin/paper";
    }


    //从x-y中的数中随机找出num个不同的数，返回给integer的动态数组中
    private ArrayList<Integer> makeRandom(int x, int y, int num)
    {
        //创建一个integer的动态数组
        ArrayList<Integer> a = new ArrayList<Integer>();
        int index = 0;
        //往数组里面逐一加取到不重复的元素
        while(index < num)
        {
            //产生x-y的随机数
            Random r = new Random();
            int temp = r.nextInt(y-x)+x ;
            //设置是否重复的标记变量为false
            boolean flag = false;
            for(int i =0; i<index;i++)
            {
                if(temp == a.get(i))
                {
                    flag = true;
                    break;
                }
            }
            if(flag==false)
            {
                a.add(temp);
                index++;
            }
        }
        return a;
    }

    @RequestMapping("/addPaperHandle")
    public String addPaperHandle(String paperId, String title, String paperLevel, String time, String questionId, Model model, HttpServletRequest request) {
        System.out.println(title);
        System.out.println(paperLevel);
        System.out.println(time);
        System.out.println(questionId);
        System.out.println(paperId);
        if (questionId != null) {
            String ids[] = questionId.split(",");
            int radioNum = 0;
            List<String> radioIdsList = new ArrayList<>();
            List<Question> radioQuestions = new ArrayList<>();
            int multipleNum = 0;
            List<String> multipleIdsList = new ArrayList<>();
            List<Question> multipleQuestions = new ArrayList<>();
            int judgeNum = 0;
            List<String> judgeIdsList = new ArrayList<>();
            List<Question> judgeQuestions = new ArrayList<>();
            int fillingNum = 0;
            List<String> fillingIdsList = new ArrayList<>();
            List<Question> fillingQuestions = new ArrayList<>();
            int essayNum = 0;
            List<String> essayIdsList = new ArrayList<>();
            List<Question> essayQuestions = new ArrayList<>();
            Question question;
            for (String id : ids) {
                question = questionService.getQuestionById(id);
                switch (question.getType()) {
                    case 1:
                        radioNum++;
                        radioIdsList.add(id);
                        radioQuestions.add(question);
                        break;
                    case 2:
                        multipleNum++;
                        multipleIdsList.add(id);
                        multipleQuestions.add(question);
                        break;
                    case 3:
                        judgeNum++;
                        judgeIdsList.add(id);
                        judgeQuestions.add(question);
                        break;
                    case 4:
                        fillingNum++;
                        fillingIdsList.add(id);
                        fillingQuestions.add(question);
                        break;
                    case 5:
                        essayNum++;
                        essayIdsList.add(id);
                        essayQuestions.add(question);
                        break;
                }
            }
            Paper paper = new Paper();
            paper.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            paper.setTitle(title);
            paper.setTime(time);
            paper.setLevel(Integer.parseInt(paperLevel));
            paper.setRadioNum(radioNum);
            StringBuilder radioIds = new StringBuilder();
            radioIdsList.forEach((id) -> radioIds.append(id).append(","));
            paper.setRadioIds(radioIds.length() > 0 ? radioIds.toString().substring(0, radioIds.length() - 1) : null);
            paper.setMultipleNum(multipleNum);
            StringBuilder multipleIds = new StringBuilder();
            multipleIdsList.forEach((id) -> multipleIds.append(id).append(","));
            paper.setMultipleIds(multipleIds.length() > 0 ? multipleIds.toString().substring(0, multipleIds.length() - 1) : null);
            paper.setJudgeNum(judgeNum);
            StringBuilder judgeIds = new StringBuilder();
            judgeIdsList.forEach((id) -> judgeIds.append(id).append(","));
            paper.setJudgeIds(judgeIds.length() > 0 ? judgeIds.toString().substring(0, judgeIds.length() - 1) : null);
            paper.setFillingNum(fillingNum);
            StringBuilder fillingIds = new StringBuilder();
            fillingIdsList.forEach((id) -> fillingIds.append(id).append(","));
            paper.setFillingIds(fillingIds.length() > 0 ? fillingIds.toString().substring(0, fillingIds.length() - 1) : null);
            paper.setEssayNum(essayNum);
            StringBuilder essayIds = new StringBuilder();
            essayIdsList.forEach((id) -> essayIds.append(id).append(","));
            paper.setEssayIds(essayIds.length() > 0 ? essayIds.toString().substring(0, essayIds.length() - 1) : null);
            paper.setState("1");
            paper.setCreateTime(new Date());
            System.out.println(paper);
            model.addAttribute("paper", paper);
            model.addAttribute("radioQuestions", radioQuestions);
            model.addAttribute("multipleQuestions", multipleQuestions);
            model.addAttribute("judgeQuestions", judgeQuestions);
            model.addAttribute("fillingQuestions", fillingQuestions);
            model.addAttribute("essayQuestions", essayQuestions);
            if (paperId != null && !Objects.equals(paperId, "")) {
                paper.setId(paperId);
                paperService.updatePaper(paper);
            } else {
                paperService.addPaper(paper);
            }
        }
        return "admin/paper";
    }

    @RequestMapping("/queryPaper")
    public String queryQuestion(String id, Model model) throws UnsupportedEncodingException {
        Paper paper = paperService.getPaperById(id);
        if (paper != null) {
            List<Question> radioQuestions = new ArrayList<>();
            List<Question> multipleQuestions = new ArrayList<>();
            List<Question> judgeQuestions = new ArrayList<>();
            List<Question> fillingQuestions = new ArrayList<>();
            List<Question> essayQuestions = new ArrayList<>();
            String[] radioIds = paper.getRadioIds().split(",");
            for (String questionId : radioIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    radioQuestions.add(questionById);
                }
            }
            String[] multipleIds = paper.getMultipleIds().split(",");
            for (String questionId : multipleIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    multipleQuestions.add(questionById);
                }
            }
            String[] judgeIds = paper.getJudgeIds().split(",");
            for (String questionId : judgeIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    judgeQuestions.add(questionById);
                }
            }
            String[] fillingIds = paper.getFillingIds().split(",");
            for (String questionId : fillingIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    fillingQuestions.add(questionById);
                }
            }
            String[] essayIds = paper.getEssayIds().split(",");
            for (String questionId : essayIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    essayQuestions.add(questionById);
                }
            }
            model.addAttribute("paper", paper);
            model.addAttribute("radioQuestions", radioQuestions);
            model.addAttribute("multipleQuestions", multipleQuestions);
            model.addAttribute("judgeQuestions", judgeQuestions);
            model.addAttribute("fillingQuestions", fillingQuestions);
            model.addAttribute("essayQuestions", essayQuestions);
            return "admin/paper";
        }
        return "redirect:/papersPage";
    }

    @RequestMapping("/takePaper")
    public String takePaper(String id, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        if (request.getSession().getAttribute("id") == null) {
            List<Paper> allPapers = paperService.getAllPapers();
            model.addAttribute("papers", allPapers);
            model.addAttribute("error", "请登录后参加考试!");
            return "index";
        }
        Paper paper = paperService.getPaperById(id);
        if (paper != null) {
            List<Question> radioQuestions = new ArrayList<>();
            List<Question> multipleQuestions = new ArrayList<>();
            List<Question> judgeQuestions = new ArrayList<>();
            List<Question> fillingQuestions = new ArrayList<>();
            List<Question> essayQuestions = new ArrayList<>();
            String[] radioIds = paper.getRadioIds().split(",");
            for (String questionId : radioIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    radioQuestions.add(questionById);
                }
            }
            String[] multipleIds = paper.getMultipleIds().split(",");
            for (String questionId : multipleIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    multipleQuestions.add(questionById);
                }
            }
            String[] judgeIds = paper.getJudgeIds().split(",");
            for (String questionId : judgeIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    judgeQuestions.add(questionById);
                }
            }
            String[] fillingIds = paper.getFillingIds().split(",");
            for (String questionId : fillingIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    fillingQuestions.add(questionById);
                }
            }
            String[] essayIds = paper.getEssayIds().split(",");
            for (String questionId : essayIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    essayQuestions.add(questionById);
                }
            }
            model.addAttribute("paper", paper);
            model.addAttribute("radioQuestions", radioQuestions);
            model.addAttribute("multipleQuestions", multipleQuestions);
            model.addAttribute("judgeQuestions", judgeQuestions);
            model.addAttribute("fillingQuestions", fillingQuestions);
            model.addAttribute("essayQuestions", essayQuestions);
            return "student/paper";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "takePaper", method = RequestMethod.POST)
    public String takePaper(String paperId, String userId, HttpServletRequest request, Model model) {
        System.out.printf("-> 交卷 : " + paperId + " - " + userId);
        Map<String, String> parameters = new HashMap<>();

        Enumeration<String> parameterNames = request.getParameterNames();

        while(parameterNames.hasMoreElements()) {

            String parameterName = parameterNames.nextElement();

            parameters.put(parameterName, request.getParameter(parameterName));
        }
        System.out.println(parameters);
        return "redirect:/";
    }

    @RequestMapping("/deletePaper")
    public String deletePaper(String id) {
        if (id != null) {
            String ids[] = id.split(",");
            for (String id1 : ids) {
                paperService.delete(id1);
            }
        }
        return "redirect:/papersPage";
    }

    @RequestMapping("/updatePaper")
    public String updatePaper(String id, Model model) throws UnsupportedEncodingException {
        Paper paper = paperService.getPaperById(id);
        if (paper != null) {
            model.addAttribute("paper", paper);
            List<Question> allQuestions = questionService.getAllQuestions();
            model.addAttribute("questions", allQuestions);
            return "admin/paperUpd";
        }
        return "redirect:/papersPage";
    }

    @RequestMapping("/downPaper")
    public ResponseEntity<byte[]> download(String paperId , HttpServletRequest request, HttpServletResponse response) throws IOException {
        Paper paper = paperService.getPaperById(paperId);
        if (paper != null) {
            List<Question> radioQuestions = new ArrayList<>();
            List<Question> multipleQuestions = new ArrayList<>();
            List<Question> judgeQuestions = new ArrayList<>();
            List<Question> fillingQuestions = new ArrayList<>();
            List<Question> essayQuestions = new ArrayList<>();
            String[] radioIds = paper.getRadioIds().split(",");
            for (String questionId : radioIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    radioQuestions.add(questionById);
                }
            }
            String[] multipleIds = paper.getMultipleIds().split(",");
            for (String questionId : multipleIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    multipleQuestions.add(questionById);
                }
            }
            String[] judgeIds = paper.getJudgeIds().split(",");
            for (String questionId : judgeIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    judgeQuestions.add(questionById);
                }
            }
            String[] fillingIds = paper.getFillingIds().split(",");
            for (String questionId : fillingIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    fillingQuestions.add(questionById);
                }
            }
            String[] essayIds = paper.getEssayIds().split(",");
            for (String questionId : essayIds) {
                Question questionById = questionService.getQuestionById(questionId);
                if (questionById != null) {
                    essayQuestions.add(questionById);
                }
            }
            File file = new File(request.getServletContext().getRealPath("/resource/uploadImg/temp.pdf"));
            Document document = new Document();
            PdfWriter writer;
            try {
                writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                BaseFont bfChinese = BaseFont.createFont( "STSongStd-Light" ,"UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
                Font font = new Font(bfChinese, 12,Font.NORMAL);
                Font font16Bold = new Font(bfChinese, 16,Font.BOLD);
                Font font10 = new Font(bfChinese, 10,Font.NORMAL);
                PdfPTable table = new PdfPTable(3);
                PdfPCell titleCell = new PdfPCell(new Paragraph(paper.getTitle(), font16Bold));
                titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell.setBorder(0);
                table.setWidths(new int[]{10, 80, 10});
                table.getDefaultCell().setBorder(0);
                table.addCell("");
                table.addCell(titleCell);
                table.addCell("");
                document.add(table);
                document.add(new Paragraph("考试时长：" + paper.getTime() + "   试卷难度：" + paper.getLevel(), font));
                document.add(new Paragraph("【单项选择题】", font));
                int num = 1;
                if (paper.getRadioNum() > 0) {
                    for (Question radioQuestion : radioQuestions) {
                        document.add(new Paragraph(num + ". " + radioQuestion.getBody() + "(  )", font10));
                        PdfPTable radioTable = new PdfPTable(2);
                        radioTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                        PdfPCell ACell = new PdfPCell(new Paragraph("A." + radioQuestion.getOptionA(), font10));
                        ACell.setBorder(0);
                        PdfPCell BCell = new PdfPCell(new Paragraph("B." + radioQuestion.getOptionB(), font10));
                        BCell.setBorder(0);
                        PdfPCell CCell = new PdfPCell(new Paragraph("C." + radioQuestion.getOptionC(), font10));
                        CCell.setBorder(0);
                        PdfPCell DCell = new PdfPCell(new Paragraph("D." + radioQuestion.getOptionD(), font10));
                        DCell.setBorder(0);
                        radioTable.setWidths(new int[]{50,50});
                        radioTable.getDefaultCell().setBorder(0);
                        radioTable.addCell(ACell);
                        radioTable.addCell(BCell);
                        radioTable.addCell(CCell);
                        radioTable.addCell(DCell);
                        document.add(radioTable);
                        document.add(new Paragraph("", font10));
                        num++;
                    }
                }
                if (paper.getMultipleNum() > 0) {
                    num = 1;
                    document.add(new Paragraph("【多项选择题】", font));
                    for (Question multipleQuestion : multipleQuestions) {
                        document.add(new Paragraph(num + ". " + multipleQuestion.getBody() + "(     )", font10));
                        PdfPTable multipleTable = new PdfPTable(2);
                        multipleTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                        PdfPCell ACell = new PdfPCell(new Paragraph(" A." + multipleQuestion.getOptionA(), font10));
                        ACell.setBorder(0);
                        PdfPCell BCell = new PdfPCell(new Paragraph(" B." + multipleQuestion.getOptionB(), font10));
                        BCell.setBorder(0);
                        PdfPCell CCell = new PdfPCell(new Paragraph(" C." + multipleQuestion.getOptionC(), font10));
                        CCell.setBorder(0);
                        PdfPCell DCell = new PdfPCell(new Paragraph(" D." + multipleQuestion.getOptionD(), font10));
                        DCell.setBorder(0);
                        multipleTable.setWidths(new int[]{50,50});
                        multipleTable.getDefaultCell().setBorder(0);
                        multipleTable.addCell(ACell);
                        multipleTable.addCell(BCell);
                        multipleTable.addCell(CCell);
                        multipleTable.addCell(DCell);
                        document.add(multipleTable);
                        document.add(new Paragraph("", font10));
                        num++;
                    }
                }
                if (paper.getJudgeNum() > 0) {
                    num = 1;
                    document.add(new Paragraph("【判断题】", font));
                    for (Question judgeQuestion : judgeQuestions) {
                        document.add(new Paragraph(num + ". " + judgeQuestion.getBody() + "(  )", font10));
                        document.add(new Paragraph("", font10));
                        num++;
                    }
                }
                if (paper.getFillingNum() > 0) {
                    num = 1;
                    document.add(new Paragraph("【填空题】", font));
                    for (Question fillingQuestion : fillingQuestions) {
                        document.add(new Paragraph(num + ". " + fillingQuestion.getBody(), font10));
                        document.add(new Paragraph("  ___________________________", font10));
                        document.add(new Paragraph("", font10));
                        num++;
                    }
                }
                if (paper.getEssayNum() > 0) {
                    num = 1;
                    document.add(new Paragraph("【问答题】", font));
                    for (Question essayQuestion : essayQuestions) {
                        document.add(new Paragraph(num + ". " + essayQuestion.getBody(), font10));
                        document.add(new Paragraph("", font10));
                        document.add(new Paragraph("", font10));
                        document.add(new Paragraph("", font10));
                        document.add(new Paragraph("", font10));
                        document.add(new Paragraph("", font10));
                        document.add(new Paragraph("", font10));
                        num++;
                    }
                }
                document.close();
                writer.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attchement;filename=" + paper.getId().substring(0, 10)+".pdf");
            HttpStatus statusCode = HttpStatus.OK;
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), httpHeaders, statusCode);
        }
        return null;
    }
}