//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Flashcard {
    private String question;
    private String answer;
    public Flashcard(){
        question = "";
        answer = "";
    }
    public Flashcard(String q, String a){
        question = q;
        answer = a;
    }
    public String getQuestion(){
        return question;
    }
    public String getAnswer(){
        return answer;
    }
}