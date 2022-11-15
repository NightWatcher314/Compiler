package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.stream.StreamSupport;

/**
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private String wordBuf;
    private final ArrayList<Token> tokenArrayList;

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.tokenArrayList = new ArrayList<>();
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        wordBuf = FileUtils.readFile(path);
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    /**
     * 0 空白状态
     * 1 2 3 4 int
     * 5 6 7 8 9 10 11 return
     * 12 13 id
     * 14 15 value
     */
    public void run() {
        int head = 0;
        int current_state = 0;
        int next_state = 0;
        char cur_char;

        for (int i = 0; i < wordBuf.length(); i++) {
            current_state = next_state;
            cur_char = wordBuf.charAt(i);
            Boolean is_letter = Character.isLetter(cur_char);
            Boolean is_digit = Character.isDigit(cur_char);
            Boolean is_whitespace = Character.isWhitespace(cur_char);
            switch (current_state) {
                case 0:
                    if (cur_char == 'i') {
                        next_state = 1;
                    } else if (cur_char == 'r') {
                        next_state = 5;
                    } else if (is_letter) {
                        next_state = 12;
                    } else if (is_digit) {
                        next_state = 14;
                    } else if (is_whitespace) {
                        next_state = 0;
                        head++;
                    } else {
                        if (cur_char == ';') {
                            tokenArrayList.add(Token.simple("Semicolon"));
                        } else {
                            tokenArrayList.add(Token.simple(String.valueOf(cur_char)));
                        }
                        head++;
                    }
                    break;
                case 1:
                    if (cur_char == 'n') {
                        next_state = 2;
                    } else if (is_letter || is_digit) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 2:
                    if (cur_char == 't') {
                        next_state = 3;
                    } else if (is_letter || is_digit) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 3:
                    if (is_whitespace) {
                        next_state = 4;
                    } else {
                        next_state = 12;
                    }
                    break;
                case 5:
                    if (cur_char == 'e') {
                        next_state = 6;
                    } else if (is_letter || is_digit) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 6:
                    if (cur_char == 't') {
                        next_state = 7;
                    } else if (is_letter || is_digit) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 7:
                    if (cur_char == 'u') {
                        next_state = 8;
                    } else if (is_letter || is_digit) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 8:
                    if (cur_char == 'r') {
                        next_state = 9;
                    } else if (is_letter || is_digit) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 9:
                    if (cur_char == 'n') {
                        next_state = 10;
                    } else if (is_letter || is_digit) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 10:
                    if (is_whitespace) {
                        next_state = 11;
                    } else {
                        next_state = 12;
                    }
                    break;
                case 12:
                    if (is_digit || is_letter) {
                        next_state = 12;
                    } else {
                        next_state = 13;
                    }
                    break;
                case 14:
                    if (is_digit) {
                        next_state = 14;
                    } else {
                        next_state = 15;
                    }
                    break;
                default:
                    break;
            }
            StringBuilder tem_string = new StringBuilder();
            if (next_state == 4 || next_state == 11 || next_state == 13 || next_state == 15) {
                while (head < i) {
                    tem_string.append(wordBuf.charAt(head++));
                }
                i--;
            }
            if (next_state == 13) {
                tokenArrayList.add(Token.normal("id", tem_string.toString()));
                if (!symbolTable.has(tem_string.toString())) {
                    symbolTable.add(tem_string.toString());
                }
                next_state = 0;
            } else if (next_state == 15) {
                tokenArrayList.add(Token.normal("IntConst", tem_string.toString()));
                next_state = 0;
            } else if (next_state == 4) {
                tokenArrayList.add(Token.simple(tem_string.toString()));
                next_state = 0;
            } else if (next_state == 11) {
                tokenArrayList.add(Token.simple(tem_string.toString()));
                next_state = 0;
            }
        }

        tokenArrayList.add(Token.eof());
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokenArrayList;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
                path,
                StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
