import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.Random;
public class FlashcardRunner {
        private JFrame frame;
        private ArrayList<Flashcard> cards;
        private GridBagConstraints gbc;
        private JPanel entranceScreen;
        private JPanel mcScreen;
        private JLabel fate;
        private Flashcard curCard;
        private JLabel questionLabel;
        private JPanel optionButtonPanel;
        private ArrayList<Flashcard> potentialOptions;
        private JPanel writeScreen;
        private JTextField answerInput;

    public FlashcardRunner(){
        frame = new JFrame("Flashcard App");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        //immediately show the entrance screen
        entranceScreen();
        frame.setVisible(true);
    }
        private void entranceScreen(){
        //removes everything (in case of coming back to screen after mode)
            frame.getContentPane().removeAll();
            entranceScreen = new JPanel(new GridBagLayout());
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 10, 10);
            entranceScreen.setBackground(new Color(238,226,223));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            JLabel titleLabel = new JLabel("Choose your mode: ");
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setFont(new Font("Courier New", Font.BOLD, 20));
            titleLabel.setForeground(new Color(47,128,228));
            entranceScreen.add(titleLabel, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            JButton mcButton = new JButton("Multiple Choice");
            mcButton.setFont(new Font("Courier New", Font.PLAIN, 12));
            mcButton.setBackground(new Color(222, 193, 219));
            entranceScreen.add(mcButton, gbc);
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            JButton writeButton = new JButton("Write");
            writeButton.setFont(new Font("Courier New", Font.PLAIN, 12));
            writeButton.setBackground(new Color(222, 193, 219));
            entranceScreen.add(writeButton, gbc);
            frame.add(entranceScreen, BorderLayout.NORTH);
            frame.revalidate();
            frame.repaint();
            //goes to corresponding method based on button clicked
            writeButton.addActionListener(e->writeMode());
            frame.setVisible(true);
            try {
                createDeck();
                //in the case of no cards or text in the flashcards.txt file
                if (cards.isEmpty()){
                    JOptionPane.showMessageDialog(frame, "Flashcards file is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                    mcButton.setEnabled(false); // Disable button if no cards
                    writeButton.setEnabled(false);
                }
            //problem with .txt
            }catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "No current flashcards!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            mcButton.addActionListener(e -> mcMode());

        }
        private void showMCQuestion(){
            //for every MC question remove the old buttons
            optionButtonPanel.removeAll();
            //remove previous output of right answer/correct
            fate.setText("");
            //after all cards done go to home screen
            if(cards.isEmpty()){
                JOptionPane.showMessageDialog(frame, "Complete", "No more flashcards", JOptionPane.INFORMATION_MESSAGE);
                entranceScreen();
                return;
            }
            //random question
            Random rand = new Random();
            curCard = cards.get(rand.nextInt(cards.size()));
            questionLabel.setText(curCard.getQuestion());

            //First time using HashSet!
            //Learned a more efficient way to avoid duplicates in the options
            Set<String> options = new HashSet<>();
            options.add(curCard.getAnswer());
            int attempts = 0;
            while (options.size() < 4 && attempts < 100) {
                String anOption = potentialOptions.get(rand.nextInt(potentialOptions.size())).getAnswer();
                options.add(anOption);
                attempts++;
            }
            ArrayList<String> presentOptions = new ArrayList<>(options);
            Collections.shuffle(presentOptions);
            //debug
            System.out.println("Options: " + options);

            for (String option:presentOptions){
                JButton choiceButton = new JButton(option);
                choiceButton.setFont(new Font("Courier New", Font.PLAIN, 12));
                choiceButton.setBackground(new Color(222, 193, 219));
                choiceButton.addActionListener(e ->{
                    if(option.equals(curCard.getAnswer())){
                        fate.setText("Correct!");
                        cards.remove(curCard);
                    } else{
                        fate.setText(curCard.getAnswer());
                        //put curCard at the end
                        cards.remove(curCard);
                        cards.add(curCard);
                    }

                });
                //create a separate panel for layout
                optionButtonPanel.add(choiceButton);
            }
            mcScreen.revalidate();
            mcScreen.repaint();
        }
        public void mcMode(){
            frame.getContentPane().removeAll();
            questionLabel = new JLabel("", SwingConstants.CENTER);
            frame.add(questionLabel, BorderLayout.NORTH);
            mcScreen = new JPanel(new BorderLayout());
            frame.add(mcScreen, BorderLayout.CENTER);
            optionButtonPanel = new JPanel(new GridLayout(2,2));
            mcScreen.add(optionButtonPanel, BorderLayout.CENTER);
            fate = new JLabel(" ");
            fate.setBackground(new Color(238, 226, 223));
            fate.setHorizontalAlignment(SwingConstants.CENTER);
            fate.setForeground(new Color(47,128,228));
            frame.add(fate, BorderLayout.SOUTH);
            JButton nextButton = new JButton("Next");
            nextButton.setBackground(new Color(109,160,225));
            nextButton.setForeground(new Color(238,226,223));
            nextButton.setFont(new Font("Courier New", Font.BOLD, 12));
            nextButton.addActionListener(e->showMCQuestion());
            mcScreen.add(nextButton, BorderLayout.SOUTH);
            frame.revalidate();
            frame.repaint();
            //for first question
            showMCQuestion();
        }

        public void createDeck() throws FileNotFoundException {
            cards = new ArrayList<>();
            potentialOptions = new ArrayList<>();
            Scanner file = new Scanner(new File("flashcards.txt"));
            while (file.hasNextLine()) {
                String extract = file.nextLine();
                Scanner chopper = new Scanner(extract);
                chopper.useDelimiter("\\|");
                String question = chopper.next();
                String answer = chopper.next();
                //gets changed throughout to know when to end
                cards.add(new Flashcard(question, answer));
                //potential options has all flashcards for options
                potentialOptions.add(new Flashcard(question, answer));
                Collections.shuffle(cards);
            }
        }
        private void showWriteQuestion(){
            writeScreen.removeAll();
            answerInput = new JTextField();
            answerInput.setFont(new Font("Courier New", Font.PLAIN, 20));
            answerInput.setBackground(new Color(222,193,219));
            answerInput.setHorizontalAlignment(JTextField.CENTER);
            answerInput.setText("");
            fate.setText("");
            if(cards.isEmpty()){
                JOptionPane.showMessageDialog(frame, "Complete", "No more flashcards", JOptionPane.INFORMATION_MESSAGE);
                entranceScreen();
                return;
            }
            Random rand = new Random();
            curCard = cards.get(rand.nextInt(cards.size()));
            questionLabel.setText(curCard.getQuestion());
            writeScreen.setLayout(new BorderLayout());
            writeScreen.add(answerInput, BorderLayout.CENTER);

            JButton submitButton = new JButton("Check?");
            submitButton.setFont(new Font("Courier New", Font.BOLD, 12));
            submitButton.setBackground(new Color(200, 170, 220));
            submitButton.setForeground(Color.BLACK);
            submitButton.addActionListener(e ->{
                if(answerInput.getText().trim().equalsIgnoreCase(curCard.getAnswer().trim())){
                    fate.setText("Correct!");
                    cards.remove(curCard);
                } else{
                    fate.setText(curCard.getAnswer());
                    cards.remove(curCard);
                    cards.add(curCard);
                }
            });
            JButton nextButton = new JButton("Next");
            nextButton.setFont(new Font("Courier New", Font.BOLD, 12));
            nextButton.setBackground(new Color(109,160,225));
            nextButton.setForeground(new Color(238,226,223));
            nextButton.addActionListener(e -> showWriteQuestion());

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(submitButton);
            buttonPanel.add(nextButton);

            writeScreen.add(buttonPanel, BorderLayout.SOUTH);

            frame.revalidate();
            frame.repaint();
            }
        public void writeMode(){
            frame.getContentPane().removeAll();
            questionLabel = new JLabel("", SwingConstants.CENTER);
            frame.add(questionLabel, BorderLayout.NORTH);
            writeScreen = new JPanel(new BorderLayout());
            frame.add(writeScreen, BorderLayout.CENTER);
            fate = new JLabel(" ");
            fate.setBackground(new Color(238, 226, 223));
            fate.setHorizontalAlignment(SwingConstants.CENTER);
            fate.setForeground(new Color(47,128,228));
            frame.add(fate, BorderLayout.SOUTH);
            frame.revalidate();
            frame.repaint();
            showWriteQuestion();
        }
        public static void main(String[] args) throws IOException {
            new FlashcardRunner();
    }
}