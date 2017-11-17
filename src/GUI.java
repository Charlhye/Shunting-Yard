import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GUI {
    private JPanel panel1;
    private JTextField outputTF;
    private JTextField inputTF;
    private JButton evaluateButton;
    private JButton stepButton;
    private JButton completeButton;
    private JTextField evalTF;
    private JPanel buttonsPanel;
    private JTable operatorStackList;
    private JButton restartB;

    private String initial;

    GUI(){
        JFrame frame=new JFrame();
        frame.setContentPane(panel1);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000,700);

        inputTF.setBorder(BorderFactory.createTitledBorder("Input"));
        outputTF.setBorder(BorderFactory.createTitledBorder("Output"));
        operatorStackList.setBorder(BorderFactory.createTitledBorder("Stack"));
        evalTF.setBorder(BorderFactory.createTitledBorder("Evaluation"));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        DefaultTableModel model=new DefaultTableModel();
        model.addColumn("Precedence");
        model.addColumn("Stack");

        operatorStackList.setModel(model);

        Queue<String> input = new LinkedList<>();
        Queue<String> output= new LinkedList<>();
        Stack<Character> operatorStack = new Stack<>();

        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inputTF.getText().equals("")){
                    try {
                        while (!operatorStack.isEmpty()) {//while there are tokens at the top of the stack
                            char runTokenStack = operatorStack.pop();
                            ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                            if (runTokenStack != '(' && runTokenStack != ')')//if they are different than parenthesis
                            {
                                output.add(String.valueOf(runTokenStack));//pop the operator and enqueue it
                                outputTF.setText("");
                                for (String car : output) {
                                    outputTF.setText(outputTF.getText()+ " " + car);
                                }
                            } else {
                                throw new ParenthesisNotPairedException("Missing parenthesis");
                            }
                        }
                        JOptionPane.showMessageDialog(null,"Terminado");
                        evaluateButton.setEnabled(true);
                    }catch (ParenthesisNotPairedException ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                    completeButton.setEnabled(false);
                    stepButton.setEnabled(false);
                    inputTF.setText(initial);
                    inputTF.setEditable(true);
                }else {
                    if(inputTF.getText().length()==input.peek().length())
                        inputTF.setText(inputTF.getText().substring(input.peek().length()));
                    else
                        inputTF.setText(inputTF.getText().substring(input.peek().length()+1));
                    try {
                        Algorithm(input.remove(), output, operatorStack);
                        System.out.print("");
                    }catch (ParenthesisNotPairedException ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (String it : input) {
                        Algorithm(it, output, operatorStack);
                    }
                    while (!operatorStack.isEmpty()) {//while there are tokens at the top of the stack
                        char runTokenStack = operatorStack.pop();
                        ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                        if (runTokenStack != '(' && runTokenStack != ')')//if they are different than parenthesis
                        {
                            output.add(String.valueOf(runTokenStack));//pop the operator and enqueue it
                            outputTF.setText("");
                            for (String car : output) {
                                outputTF.setText(outputTF.getText()+ " " + car);
                            }
                        } else {
                            throw new ParenthesisNotPairedException("Missing parenthesis");
                        }
                    }
                    JOptionPane.showMessageDialog(null,"Terminado");
                    evaluateButton.setEnabled(true);
                }catch (ParenthesisNotPairedException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    clear(input, output,operatorStack);
                }
                completeButton.setEnabled(false);
                stepButton.setEnabled(false);
                inputTF.setEditable(true);
                inputTF.setText(initial);
            }
        });
        restartB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear(input,output,operatorStack);
                inputTF.setText(spaces(inputTF.getText()));
                for (String it:inputTF.getText().split(" ")) {
                    input.add(it);
                }
                inputTF.setEditable(false);
                initial=inputTF.getText();
                completeButton.setEnabled(true);
                stepButton.setEnabled(true);
                evaluateButton.setEnabled(false);
            }
        });
        evaluateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Queue <String> toeval=new LinkedList<>();
                Stack <Integer> numStack=new Stack<>();

                for (String it:outputTF.getText().split(" ")) {
                    if (!it.equals(""))
                        toeval.add(it);
                }
                while (!toeval.isEmpty()){
                    String token=toeval.remove();
                    if(token.charAt(0) == '+'){
                        numStack.push(numStack.pop()+numStack.pop());
                    }else if(token.charAt(0) == '-'){
                        numStack.push(numStack.pop()-numStack.pop());
                    }else if(token.charAt(0) == '*') {
                        numStack.push(numStack.pop()*numStack.pop());
                    }else if(token.charAt(0) == '/'){
                        numStack.push(numStack.pop()/numStack.pop());
                    }else if(token.charAt(0) == '^'){
                        int a=numStack.pop();
                        numStack.push((int)Math.pow(numStack.pop(),a));
                    }else {
                        numStack.push(Integer.parseInt(String.valueOf(token)));
                    }
                }
                evalTF.setText(String.valueOf(numStack.pop()));
            }
        });
    }

    private void clear(Queue<String> input, Queue<String> output, Stack<Character> operatorStack ){
        while(!input.isEmpty()){
            input.remove();
        }
        while(!output.isEmpty()){
            output.remove();
        }
        outputTF.setText("");
        while(!operatorStack.isEmpty()){
            operatorStack.pop();
        }
        int rows=operatorStackList.getModel().getRowCount();
        for (int i = 0; i < rows; i++) {
            ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
        }
    }

    private void Algorithm(String token, Queue<String> output, Stack<Character> operatorStack) throws ParenthesisNotPairedException {
        if(isNumeric(token)){//If token is a number
            output.add(token);//enqueue it
            outputTF.setText("");
            for (String car:output) {
                outputTF.setText(outputTF.getText()+" "+car);
            }
        }
        else if(token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")) {//If token is an operator o1
            if (!operatorStack.isEmpty() && operatorStack.peek() != '(') {//This excludes the open parenthesis
                while (!operatorStack.isEmpty() && (!token.equals("^") && Operator.precedence(token.charAt(0), operatorStack.peek()) <= 0) || (!token.equals("^") && Operator.precedence(token.charAt(0), operatorStack.peek()) < 0)) {//while there is a operator in the top of the stack and o1 is left associative and its precedence is less o equal than o2 or o1 is right associative and its precedence is less than o2
                    output.add(String.valueOf(operatorStack.pop()));//pop o2 from the stack and enqueue it
                    ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                    outputTF.setText("");
                    for (String car:output) {
                        outputTF.setText(outputTF.getText()+" "+car);
                    }
                }
            }
            operatorStack.push(token.toCharArray()[0]);
            ((DefaultTableModel) operatorStackList.getModel()).insertRow(0,new String[]{String.valueOf(Operator.myprecedence(token.charAt(0))),String.valueOf(token)});
        }else if (token.equals("(")){//if token is an open parenthesis
            operatorStack.push(token.toCharArray()[0]);// push it in the top of the stack
            ((DefaultTableModel) operatorStackList.getModel()).insertRow(0,new String[]{String.valueOf(Operator.myprecedence(token.charAt(0))),String.valueOf(token)});
        }else if (token.equals(")")){//if token is an close parenthesis
            char runTokenStack;
            //try catch if there is a parenthesis without its partner
            try {
                while ((runTokenStack = operatorStack.pop()) != '(') {//Until the top of the stack is an open parenthesis
                    ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                    output.add(String.valueOf(runTokenStack));//pop operands from the stack and enqueue them
                    outputTF.setText("");
                    for (String car : output) {
                        outputTF.setText(outputTF.getText()+" " + car);
                    }
                }
            }catch (EmptyStackException ex){
                throw new ParenthesisNotPairedException("Missing parenthesis");
            }
            ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
        }
        //If there are no more tokens to read
    }

    public String spaces(String toEval){
        Queue <String> in=new LinkedList<>();
        for (char it:toEval.toCharArray()) {
            in.add(String.valueOf(it));
        }
        Stack <String> out=new Stack<>();
        while (!in.isEmpty()){
            String token=in.remove();
            if(token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")){
                out.push(token);
            }else{
                if(!out.isEmpty()){
                    if (out.peek().equals("^") || out.peek().equals("*") || out.peek().equals("/") || out.peek().equals("+") || out.peek().equals("-")){
                        out.push(token);
                    }else{
                        out.push(out.pop()+token);
                    }
                }else {
                    out.push(token);
                }
            }
        }
        String res="";
        while (!out.isEmpty()){
            if(out.size()!=1){
                res=" "+out.pop()+res;
            }else {
                res=out.pop()+res;
            }
        }
        return res;
    }

    public boolean isNumeric(String str){
        try{
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }
}
