package view;

import compiler.Mean;
import compiler.Lexical;
import compiler.Grammar;
import compiler.basic.Token;
import compiler.exception.WordAnalysisException;
import compiler.exception.GrammarAnalysisException;
import compiler.symbol.VN;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.Alert.AlertType.WARNING;

public class Controller {

    String codePath = "";
    String result1_Path = "D:\\202025310107 郭蕙宁\\mycompiler\\src\\result\\result1.txt";
    //String result2_Path = "D:\\7788\\study\\大三上\\编译原理\\compiler-master\\src\\result\\result2.txt";
    String result3_Path = "D:\\202025310107 郭蕙宁\\mycompiler\\src\\result\\result3.txt";
    File selectedFile;
    boolean hasFile = false;

    public List<Token> getWordResult() {
        return wordResult;
    }

    public List<String> getTranslateResult() {
        return translateResult;
    }

    private List<Token> wordResult;  //词法分析结果
    private List<String> translateResult; //语义分析，三地址代码结果
    private static VN program;
    Lexical lexical;

    public char[] getCode() {
        return code;
    }

    char[] code = null;
    char[] buffer = null;
    @FXML
    private Button grammar;
    @FXML
    private Button mean;
    @FXML
    private Button openfile;
    @FXML
    private TextArea text;
    @FXML
    private TextArea result;
    @FXML
    private Button word;

    /**
     * 打开文件
     *
     * @param event
     */
    //打开按钮
    @FXML
    void openFile_Action(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        Stage stage = (Stage) text.getScene().getWindow(); // 获取当前窗口的Stage
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            codePath = selectedFile.getAbsolutePath();
            hasFile = true;
            code = openFile(codePath);
            displayFileContent(text, code);
            tip("成功打开文件");
            Alert alert = new Alert(INFORMATION, "【成功打开文件】");
            alert.show();
        } else {
            warn("未选择文件");
        }
    }

    public static char[] openFile(String path) {
        char[] code = getCode(path);
        showCode(code);
        return preprocess(code);
    }

    /********
     *  词法分析
     * @param event
     * @throws GrammarAnalysisException
     */
    //词法分析
    @FXML
    void wordAnalyze_Action(ActionEvent event) {
        action1();
    }

    //词法分析
    void action1() {
        code = openFile(codePath);
        wordResult = lexicalAnalysis(code);
        //displayResult(analysisResult);
        //写进文件
        if (wordResult != null) {
            writeToFile1(result1_Path, wordResult); // 将语法分析结果写入result1
            buffer = openFile(result1_Path);
            displayFileContent(result, buffer);
        }
    }

    public List<Token> lexicalAnalysis(char[] code) {
        try {
            lexical = new Lexical(code); //词法分析对象
            List<Token> result1 = lexical.getResult();//创一个tokenlist获取词法分析结果
            tip("词法分析成功!");
            Alert alert = new Alert(INFORMATION, "【词法分析成功】\n"+"词法分析结果已写入文件：" + result1_Path);
            alert.show();
            return result1;//返回给全局变量
        } catch (WordAnalysisException e) {
            warn("词法分析错误！" + e.errorMessage);
            Alert alert = new Alert(WARNING, "【词法错误】：" + e.errorMessage + "\n行:" + e.row + " 列:" + e.col);
            alert.show();
            warn("行:" + e.row + " 列:" + e.col);
            return null;
        }
    }

    /********
     *  语法分析
     * @param event
     * @throws GrammarAnalysisException
     */
    //语法分析,将错误信息写入result2，success或者哪里错了。
    @FXML
    void grammarAnalyze_Action(ActionEvent event) throws GrammarAnalysisException {
        //action1();
        //wordAnalyze_Action(event);
        action2();
    }

    //语法分析
    void action2() throws GrammarAnalysisException {
        code = getCode();
        semanticAnalysis(code);
        /*writeToFile(analysisResult); // 将语法分析结果写入文件1
        code = openFile(result1_Path);
        displayFileContent(result,code);*/
    }

    public static void warn(String s) {
        //System.out.println(Utils.toRed(s));
        System.out.println(s);
    }

    public void semanticAnalysis(char[] code) {
        try {
            Lexical lexical = new Lexical(code);
            List<Token> result = lexical.getResult();
            if (wordResult != null) {
                writeToFile1(result1_Path, wordResult); // 将语法分析结果写入result1
                buffer = openFile(result1_Path);//打开result1
                //displayFileContent(wordResult,buffer);
            }
            tip("词法分析成功!");
            Grammar grammar = new Grammar();
            VN program = grammar.getProgram(result);
            //Utils.showVN(program);
            tip("语法分析成功!");
            Alert alert = new Alert(INFORMATION, "【语法分析成功】语法正确！");
            alert.show();
        } catch (WordAnalysisException e) {
            warn("词法分析错误！" + e.errorMessage);
            warn("行:" + e.row + " 列:" + e.col);
            Alert alert = new Alert(WARNING, "【语法分析失败】" + "\n" + "【存在词法错误】：" + e.errorMessage + "\n行:" + e.row + " 列:" + e.col);
            alert.show();
        } catch (GrammarAnalysisException e) {
            warn(e.getMessage());
            Alert alert = new Alert(WARNING, "【语法分析失败】" + "\n" + "【存在语法错误】：" + e.getMessage());
            alert.show();
        }
//        try {
//            Semantic semantic = new Semantic();
//            VN program = semantic.getProgram(wordResult);
//            Utils.showVN(program);
//            tip("语法分析成功!");
//            Alert alert = new Alert(INFORMATION,"语法分析成功!");
//            alert.show();
//        } catch (SemanticAnalysisException e) {
//            warn(e.getMessage());
//            //有问题，语法报错具体说第几行比较好
//            Alert alert = new Alert(WARNING,"语法分析失败!"+"\n"+e.getMessage());
//            alert.show();
//        }
    }

    /********
     *语义分析
     * @param
     * @throws GrammarAnalysisException
     */
    //语义分析
    @FXML
    void meanAnalyze_Action(ActionEvent event) {
        action3();
    }

    void action3() {
        code = getCode();
        meanAnalysis(code);//这个返回的stringList写进文件
        //写进文件
        writeToFile3(result3_Path, translateResult); // 将语法分析结果写入result3
//        buffer = openFile(result1_Path);
//        displayFileContent(result, buffer);

    }

    private void writeToFile3(String filePath, List<String> translateResult) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("");
            for (String s : translateResult) {
                writer.write(s + "\n"); // 将结果逐行写入文件
            }
            //System.out.println("语义分析后的三地址代码已写入文件：" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //语义分析
    public void meanAnalysis(char[] code) {
        try {
            Lexical lexical = new Lexical(code);
            wordResult = lexical.getResult();
            if (wordResult != null) {
                writeToFile1(result1_Path, wordResult); // 将语法分析结果写入result1
                //buffer = openFile(result1_Path);//打开result1
                //displayFileContent(wordResult,buffer);
            }
            tip("词法分析成功!");
            Grammar grammar = new Grammar();
            VN program = grammar.getProgram(wordResult);
            //showVN(program);
            tip("语法分析成功!");
            Mean mean = new Mean();
            translateResult = mean.translate(program);//返回的是String的list
            for (String s : translateResult) {
                System.out.println(s);
            }
            tip("语义分析成功!"+"\n"+"语义分析的三地址代码结果已写入文件：" + result3_Path);
            Alert alert = new Alert(INFORMATION, "【语义分析成功】\n"+"分析结果已写入文件：\n" + result3_Path);
            alert.show();
            //return translateResult;
        } catch (WordAnalysisException e) {
            warn("词法分析错误！" + e.errorMessage);
            warn("行:" + e.row + " 列:" + e.col);
            Alert alert = new Alert(WARNING, "【分析失败！】" + "\n" + "【词法错误】：" + e.errorMessage + "\n行:" + e.row + " 列:" + e.col);
            alert.show();
        } catch (GrammarAnalysisException e) {
            Alert alert = new Alert(WARNING, "【分析失败！】" + "\n" + "【语法错误】：" + e.getMessage());
            alert.show();
        }
        /*for (String s : translation) {
            System.out.println(s);
        }*/
    }

    //
    public static void showCode(char[] code) {
        int lineCount = 1;
        System.out.println("-----------------------");
        System.out.print("[1]  ");
        for (char ch : code) {
            System.out.print(ch);
            if (ch == '\n') {
                lineCount = lineCount + 1;
                System.out.print("[" + lineCount + "]  ");
            }
        }
        System.out.println();
        System.out.println("-----------------------");
    }

    //public static void lexicalAnalysis(char[] code) {






    //读取某个文件然后在text展示
    private void displayFileContent(TextArea text,char[] content) {
        StringBuilder contentBuilder = new StringBuilder();
        for (char ch : content) {
            contentBuilder.append(ch);
        }
        text.setText(contentBuilder.toString());
    }
    /*public void writeToFile(String filePath,List<Token> analysisResult) {
        String filePath = "src/result1.txt"; // 文件路径，根据你的实际需求修改
        try (FileWriter writer = new FileWriter(filePath,false)) {
            for (Token token : analysisResult) {
                writer.write(token.toString() + "\n"); // 将结果逐行写入文件
            }
            System.out.println("词法分析结果已写入文件：" + filePath);
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }*/
    public void writeToFile1(String filePath, List<Token> analysisResult) {
        //String filePath = "src/result1.txt"; // 文件路径，根据你的实际需求修改
        try (FileWriter writer = new FileWriter(filePath,false)) {
            writer.write("");
            for (Token token : analysisResult) {
                writer.write(token.toString() + "\n"); // 将结果逐行写入文件
            }
            System.out.println("词法分析结果已写入文件：" + filePath);
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public static void tip(String s) {
        System.out.println(s);
    }

    public static char[] getCode(String path) {
        String code = "";
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int len;
            while ((len = fr.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
            sb.append('\0');
            code = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toCharArray();
    }

    public static char[] preprocess(char[] code) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < code.length; i++) {
            if (code[i] == '#' || (code[i] == '/' && i < code.length - 1 && code[i + 1] == '/')) {
                while (i < code.length && code[i] != '\n') {
                    i++;
                }
                i--;
            } else {
                sb.append(code[i]);
            }
        }
        return sb.toString().toCharArray();
    }

}


    //
/*    private void displayResult(List<Token> analysisResult) {
        if (analysisResult != null) {
            StringBuilder resultBuilder = new StringBuilder();
            for (Token token : analysisResult) {
                resultBuilder.append(token).append("\n");
            }
            result.setText(resultBuilder.toString()); // 将结果显示在右侧的 TextArea 中
        } else {
            // 处理分析失败或出错的情况
            result.setText("词法分析失败或出错！");
        }
    }

    public static String openFileUI() {
        while (true) {
            System.out.print(Utils.highlightKeyWord("请输入文件路径") + ":");
            Scanner scanner = new Scanner(System.in);
            String path = scanner.nextLine();
            File file = new File(path);
            if (file.exists()) {
                return path;
            }
            warn("文件不存在!");
        }
    }*/
