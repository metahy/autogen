<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>QuestionPage</title>
    <link type="text/css" rel="stylesheet" href="${ctx}/resource/bootstrap-3.3.7-dist/css/bootstrap.min.css"/>
    <style>
        .radio {
            margin: 0;
        }

        .checkbox {
            margin: 0;
        }

        p {
            margin: 0;
        }

        input:focus {
            outline: none;
        }

        .questionType {
            font-size: 16px;
            font-weight: 900;
        }
    </style>
</head>
<body>
<div>
    <nav class="navbar navbar-default" style="margin-bottom: 0px;">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                        data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/">C语言课程自动组卷系统</a>
            </div>

            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <li>
                        <%
                            if (session.getAttribute("id") == null) {
                        %>
                        <div class="btn-group" role="group" aria-label="...">
                            <a type="button" class="btn btn-link navbar-btn" data-toggle="modal" data-target="#myModal">登录</a>
                            <a type="button" class="btn btn-link navbar-btn" data-toggle="modal"
                               data-target="#myModal2">注册</a>
                        </div>
                        <%
                        } else {
                        %>
                        <div class="btn-group" role="group" aria-label="...">
                            <a href="/" type="button"
                               class="btn btn-link navbar-btn"><%=session.getAttribute("realName")%>
                            </a>
                            <a href="${ctx}/logout" class="btn btn-link navbar-btn">注销</a>
                        </div>
                        <%
                            }
                        %>
                    </li>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>
    <div class="container">
        <div style="position: fixed; bottom: 0; right: 0; font-size: 20px; color: red;">
            <strong id="hour_show">0时</strong>
            <strong id="minute_show">0分</strong>
            <strong id="second_show">0秒</strong>
            <button class="btn btn-success" onclick="submitPaper()">交卷</button>
        </div>
        <div>
            <h1>${paper.title}
                <span style="color: gray; font-size: 14px;">
                    <span style="color: gray; font-style: italic;">时长：${paper.time}分钟&nbsp;&nbsp;&nbsp;&nbsp;试卷难度：
                        <c:if test="${paper.level==1}">简单</c:if>
                        <c:if test="${paper.level==2}">一般</c:if>
                        <c:if test="${paper.level==3}">中等</c:if>
                        <c:if test="${paper.level==4}">较难</c:if>
                        <c:if test="${paper.level==5}">巨难</c:if>
                        <a href="${ctx}/downPaper?paperId=${paper.id}">导出试卷</a>
                    </span>
                </span>
            </h1>
        </div>
        <form action="${ctx}/takePaper" id="paperForm" method="post">
            <input type="hidden" name="paperId" value="${paper.id}">
            <input type="hidden" name="userId" value="<%=session.getAttribute("id")%>">
            <div style="width: 600px; margin: auto;">
                <span style="color: red; margin-left: 10px;">Tips : 答题过程中刷新页面个将直接提交试卷！</span>
                <c:if test="${paper.radioNum>0}">
                    <div style="margin: 10px;">
                        <p class="questionType">【单项选择题】</p>
                        <c:forEach items="${radioQuestions}" var="question" varStatus="statu">
                            <div style="margin: 10px 0;">
                                <p>${statu.count}.${question.body}</p>
                                <c:if test="${question.type==1}">
                                    <table style="width: 100%; margin-left: 10px;">
                                        <tr>
                                            <td>
                                                <c:if test="${question.optionNum>=1}">
                                                    <div class="radio">
                                                        <label>
                                                            <input class="radio" type="radio" name="${question.id}"
                                                                   id="optionA"
                                                                   value="A">A.${question.optionA}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${question.optionNum>=2}">
                                                    <div class="radio">
                                                        <label>
                                                            <input type="radio" name="${question.id}" id="optionB"
                                                                   value="B">B.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <c:if test="${question.optionNum>=3}">
                                                    <div class="radio">
                                                        <label>
                                                            <input type="radio" name="${question.id}" id="optionC"
                                                                   value="C">C.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${question.optionNum>=4}">
                                                    <div class="radio">
                                                        <label>
                                                            <input type="radio" name="${question.id}" id="optionD"
                                                                   value="D">D.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="2">
                                                <c:if test="${question.optionNum==5}">
                                                    <div class="radio">
                                                        <label>
                                                            <input type="radio" name="${question.id}" id="optionE"
                                                                   value="E">E.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </table>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${paper.multipleNum>0}">
                    <div style="margin: 10px;">
                        <p class="questionType">【多项选择题】</p>
                        <c:forEach items="${multipleQuestions}" var="question" varStatus="statu">
                            <div style="margin: 10px 0;">
                                <p>${statu.count}.${question.body}</p>
                                <c:if test="${question.type==2}">
                                    <table style="width: 100%; margin-left: 10px;">
                                        <tr>
                                            <td>
                                                <c:if test="${question.optionNum>=1}">
                                                    <div class="checkbox">
                                                        <label>
                                                            <input type="checkbox" name="${question.id}" id="optionA"
                                                                   value="A">A.${question.optionA}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${question.optionNum>=2}">
                                                    <div class="checkbox">
                                                        <label>
                                                            <input type="checkbox" name="${question.id}" id="optionB"
                                                                   value="B">B.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <c:if test="${question.optionNum>=3}">
                                                    <div class="checkbox">
                                                        <label>
                                                            <input type="checkbox" name="${question.id}" id="optionC"
                                                                   value="C">C.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${question.optionNum>=4}">
                                                    <div class="checkbox">
                                                        <label>
                                                            <input type="checkbox" name="${question.id}" id="optionD"
                                                                   value="D">D.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="2">
                                                <c:if test="${question.optionNum==5}">
                                                    <div class="checkbox">
                                                        <label>
                                                            <input type="checkbox" name="${question.id}" id="optionE"
                                                                   value="E">E.${question.optionB}
                                                        </label>
                                                    </div>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </table>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${paper.judgeNum>0}">
                    <div style="margin: 10px;">
                        <p class="questionType">【判断题】</p>
                        <c:forEach items="${judgeQuestions}" var="question" varStatus="statu">
                            <div style="margin: 10px 0;">
                                <p>${statu.count}.${question.body}</p>
                                <c:if test="${question.type==3}">
                                    <table style="width: 100%; margin-left: -10px;">
                                        <tr>
                                            <td>
                                                <div class="checkbox">
                                                    <label>
                                                        <input type="radio" name="${question.id}" value="true"> 正确
                                                    </label>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="checkbox">
                                                    <label>
                                                        <input type="radio" name="${question.id}" value="false"> 错误
                                                    </label>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${paper.fillingNum>0}">
                    <div style="margin: 10px;">
                        <p class="questionType">【填空题】</p>
                        <c:forEach items="${fillingQuestions}" var="question" varStatus="statu">
                            <div style="margin: 10px 0;">
                                <p>${statu.count}.${question.body}</p>
                                <c:if test="${question.type==4}">
                                    <div style="margin-left: 15px;">
                                        答案： <input type="text" id="filling"
                                                   style="border: none; border-bottom:1px solid #000; width: 300px;"
                                                   name="${question.id}">
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${paper.essayNum>0}">
                    <div style="margin: 10px;">
                        <p class="questionType">【问答题】</p>
                        <c:forEach items="${essayQuestions}" var="question" varStatus="statu">
                            <div style="margin: 10px 0;">
                                <p>${statu.count}.${question.body}</p>
                                <c:if test="${question.type==5}">
                                    <table style="width: 100%; margin-left: 15px;">
                                        <tr>
                                            <td style="vertical-align: top; width: 50px;">
                                                答案：
                                            </td>
                                            <td>
                                                <textarea class="form-control" id="essay" name="${question.id}"
                                                          rows="3"></textarea>
                                            </td>
                                        </tr>
                                    </table>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </form>
    </div>
</div>
<script src="${ctx}/resource/js/jquery-3.2.1.min.js"></script>
<script src="${ctx}/resource/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
<script>
    var intDiff = parseInt(${paper.time*60});//倒计时总秒数量
    function timer(intDiff) {
        window.setInterval(function () {
            var day = 0,
                hour = 0,
                minute = 0,
                second = 0;//时间默认值
            if (intDiff > 0) {
                day = Math.floor(intDiff / (60 * 60 * 24));
                hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
                minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
                second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
            } else {
                submitPaper();
            }
            if (minute <= 9) minute = '0' + minute;
            if (second <= 9) second = '0' + second;
            $('#hour_show').html('<s id="h"></s>' + hour + '时');
            $('#minute_show').html('<s></s>' + minute + '分');
            $('#second_show').html('<s></s>' + second + '秒');
            intDiff--;
        }, 1000);
    }

    $(function () {
        timer(intDiff);
    });

    function submitPaper() {
        $("#paperForm").submit();
    }

    function closeIt() {
        $("#paperForm").submit();
    }

    window.onbeforeunload = submitPaper;
</script>
</body>
</html>