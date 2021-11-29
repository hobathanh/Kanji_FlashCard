package team.loser.kanjiflashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import team.loser.kanjiflashcard.models.Card;
import team.loser.kanjiflashcard.models.Question;

public class QuizActivity extends AppCompatActivity {
    private TextView btnOption1, btnOption2, btnOption3, btnOption4;
    private TextView tvQuesIndex, tvQuestion;
    private DatabaseReference allCardsRef;
    private ArrayList<Card> mListCards;
    private ArrayList<Question> mListQuestions;
    private ArrayList<String> mListOptions;
    int questNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setControls();
        getAllCards();
        setEvents();
    }


    private void setControls() {
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);
        tvQuesIndex = findViewById(R.id.tv_question_index);
        tvQuestion = findViewById(R.id.tv_question);
        mListCards = new ArrayList<>();
        mListOptions = new ArrayList<>();
        mListQuestions = new ArrayList<>();

        Intent intent = getIntent();
        String categoryId = intent.getStringExtra("CATEGORY_ID");
        allCardsRef = MainActivity.reference.child(categoryId).child("flashCards");

    }
    int selectedOption = -1;
    private void setEvents() {
        btnOption1.setOnClickListener(view -> {
            selectedOption =0;
            checkAnswer(selectedOption);
        });
        btnOption2.setOnClickListener(view -> {
            selectedOption =1;
            checkAnswer(selectedOption);
        });
        btnOption3.setOnClickListener(view -> {
            selectedOption =2;
            checkAnswer(selectedOption);
        });
        btnOption4.setOnClickListener(view -> {
            selectedOption =3;
            checkAnswer(selectedOption);
        });
    }

    private void checkAnswer(int selectedOption) {
        if(selectedOption == 0){
            btnOption1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
        if(selectedOption == 1){
            btnOption2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
        if(selectedOption == 2){
            btnOption3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
        if(selectedOption == 3){
            btnOption4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
        if(selectedOption == mListQuestions.get(questNum).getCorrectAns()){
            //correct
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // do something
                    changeQuestion();
                }
            }, 1000);
        }
        else{
            //wrong
//            Toast.makeText(QuizActivity.this, "False", Toast.LENGTH_SHORT).show();
            if(selectedOption == 0){
                btnOption1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
            if(selectedOption == 1){
                btnOption2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
            if(selectedOption == 2){
                btnOption3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
            if(selectedOption == 3){
                btnOption4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
    }

    private void changeQuestion() {
        btnOption1.setBackgroundTintList(null);
        btnOption2.setBackgroundTintList(null);
        btnOption3.setBackgroundTintList(null);
        btnOption4.setBackgroundTintList(null);
        if(questNum < mListQuestions.size() -1){
            questNum++;
            setQuestion(questNum);
        }
        else {
            //end quizz
        }
    }

    private void getAllCards() {
        allCardsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Card card = dataSnapshot.getValue(Card.class);
                    mListCards.add(card);
                    mListOptions.add(card.getDefinition());
                }
                mListQuestions = getQuestionListForQuiz();
//                Toast.makeText(QuizActivity.this, mListQuestions.size() + "", Toast.LENGTH_SHORT).show();
                setQuestion(questNum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizActivity.this, "can't get list of cards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setQuestion(int i) {
        tvQuestion.setText(mListQuestions.get(i).getQuestion());
        btnOption1.setText(mListQuestions.get(i).getOption1());
        btnOption2.setText(mListQuestions.get(i).getOption2());
        btnOption3.setText(mListQuestions.get(i).getOption3());
        btnOption4.setText(mListQuestions.get(i).getOption4());
        tvQuesIndex.setText(i+1+"/"+mListQuestions.size());
    }

    private ArrayList<Question> getQuestionListForQuiz() {
        if (mListCards.size() == 0) return null;
        ArrayList<Question> listQues = new ArrayList<>();
        for (Card card : mListCards) {
            String ques = card.getTerm();
            String ans = card.getDefinition();
            Question question = makeOneQuestion(ques, ans, mListOptions);
            listQues.add(question);
        }
        return listQues;
    }

    private Question makeOneQuestion(String ques, String ans, ArrayList<String> allOptions) {
        Question resQuestion = new Question();
        resQuestion.setQuestion(ques); // question
        String[] options = new String[4];
        Random generator = new Random();
        int indexOfAnswer = generator.nextInt(4);
        resQuestion.setCorrectAns(indexOfAnswer);// answer
        options[indexOfAnswer] = ans;
        if (indexOfAnswer == 0) {
            resQuestion.setOption1(ans);
            ArrayList<Integer> indexOfAnotherOptions = getAnotherOptions(ans, mListOptions);
            resQuestion.setOption2(mListOptions.get(indexOfAnotherOptions.get(0)));
            resQuestion.setOption3(mListOptions.get(indexOfAnotherOptions.get(1)));
            resQuestion.setOption4(mListOptions.get(indexOfAnotherOptions.get(2)));
        }
        if (indexOfAnswer == 1) {
            resQuestion.setOption2(ans);
            ArrayList<Integer> indexOfAnotherOptions = getAnotherOptions(ans, mListOptions);
            resQuestion.setOption1(mListOptions.get(indexOfAnotherOptions.get(0)));
            resQuestion.setOption3(mListOptions.get(indexOfAnotherOptions.get(1)));
            resQuestion.setOption4(mListOptions.get(indexOfAnotherOptions.get(2)));
        }
        if (indexOfAnswer == 2) {
            resQuestion.setOption3(ans);
            ArrayList<Integer> indexOfAnotherOptions = getAnotherOptions(ans, mListOptions);
            resQuestion.setOption2(mListOptions.get(indexOfAnotherOptions.get(0)));
            resQuestion.setOption1(mListOptions.get(indexOfAnotherOptions.get(1)));
            resQuestion.setOption4(mListOptions.get(indexOfAnotherOptions.get(2)));
        }
        if (indexOfAnswer == 3) {
            resQuestion.setOption4(ans);
            ArrayList<Integer> indexOfAnotherOptions = getAnotherOptions(ans, mListOptions);
            resQuestion.setOption2(mListOptions.get(indexOfAnotherOptions.get(0)));
            resQuestion.setOption3(mListOptions.get(indexOfAnotherOptions.get(1)));
            resQuestion.setOption1(mListOptions.get(indexOfAnotherOptions.get(2)));
        }

        return resQuestion;
    }

    private ArrayList<Integer> getAnotherOptions(String answer, ArrayList<String> allOptions) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        Random randomGenerator = new Random();
        while (numbers.size() < 3) {

            int random = randomGenerator.nextInt(allOptions.size());
            if (!numbers.contains(random) && allOptions.get(random) != answer) {
                numbers.add(random);
            }
        }
        return numbers;
    }

}