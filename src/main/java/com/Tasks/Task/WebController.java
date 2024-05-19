package com.Tasks.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.net.Authenticator;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login(){
        return "index.html";
    }
    @GetMapping("/home")
    public String home(){
        return "home.html";
    }
    @GetMapping("/newTask")
    public String newTask(){
        return "newTask.html";
    }
    @GetMapping("/myTasks")
    public String myTasks(){
        return "myTasks.html";
    }

    private final MySql mySql;

    @GetMapping("/choiceTask")
    public String choiceTask(){
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        String[] target = new String[] {"task1","task2","task3","task4"};
        String[] from = new String[] {"users"};
        String[] when = new String[] {"username"};
        String[] whenValue = new String[] {login};
        String[] titles = mySql.get(target,from,when,whenValue,true);
        for(String i :titles){
            System.out.println(i);
        }
        return "i";
    }

    @Autowired
    public WebController(DataSource dataSource){
        this.mySql = new MySql(dataSource);
    }
    @PostMapping("/addTask")
    public String addTask(@RequestParam String title, @RequestParam String content,@RequestParam String slot){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        String[] tasksNameValues = new String[] {"title","content","owner"};
        String[] tasksValues = new String[] {title,content,login};
        String[] usersNameValues = new String[] {"task"+slot,"username"};
        mySql.put("tasks",tasksNameValues,tasksValues);
        String[] target = new String[] {"id"};
        String[] from = new String[] {"tasks"};
        String[] when = new String[] {"owner","title"};
        String[] whenValue = new String[] {login,title};
        String taskId = mySql.get(target,from,when,whenValue);
        String[] usersValues = new String[] {taskId,login};
        mySql.singleUpdate("users",usersNameValues,usersValues);
        return "home.html";
    }
    @PostMapping("/viewTask")
    @ResponseBody
    public String viewTask(@RequestParam String taskNumber){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();

        String[] target = new String[] {"task"+taskNumber};
        String[] from = new String[] {"users"};
        String[] when = new String[] {"username"};
        String[] whenValue = new String[] {login};
        String taskId = mySql.get(target,from,when,whenValue);

        String[] target2 = new String[] {"content"};
        String[] from2 = new String[] {"tasks"};
        String[] when2 = new String[] {"id"};
        String[] whenValue2 = new String[] {taskId};
        String content = mySql.get(target2,from2,when2,whenValue2);

        String title = "title";
        if(content.equals("")||content==null){
            content = "Brak notatki(Miejsce nr"+taskNumber+" jest wolne)";
        }
        else{
            String[] target3 = new String[] {"title"};
            String[] from3 = new String[] {"tasks"};
            String[] when3 = new String[] {"id"};
            String[] whenValue3 = new String[] {taskId};
            title = mySql.get(target3,from3,when3,whenValue3);
        }
        String page = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Login</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "            background-color: #f3f3f3;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            border: 2px solid #ccc;\n" +
                "            border-radius: 8px;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            width: 50%;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            font-size: 200%;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>"+title+"</h1>\n" +
                "<div class=\"container\">\n" +
                "    <p>"+content+"</p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
        return page;
    }
}
