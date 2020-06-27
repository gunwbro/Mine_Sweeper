package Mine_Sweeper;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class Mine_Sweeper extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int rows_;
    private final int cols_;   // 행과 열
    private final int totalNum;
    private final int[] generNum; // 랜덤 숫자 생성을 위한 배열
    private final int[] numList;
    private final int mine; // 마인의 갯수
    private int leftFlag; // 깃발 수
    private int time = 0; // 게임 시간
    private int clickedNum = 0;
    Button[] j;
    JPanel upPanel;
    JPanel downPanel;
    JTextField mineNum;
    JTextField timerField;

    // 생성자
    Mine_Sweeper(int r, int c) {
        setTitle("지뢰찾기");
        setLayout(new BorderLayout());

        // 변수 초기화
        rows_ = r;
        cols_ = c;
        totalNum = r*c;
        generNum = new int[totalNum + 1];
        numList = new int[totalNum];
        mine = totalNum / 10;
        leftFlag = mine;
        upPanel = new JPanel();
        downPanel = new JPanel();
        downPanel.setLayout(new GridLayout(rows_, cols_)); // 레이아웃을 설정

        ////////////// 상단 UI /////////////////////////////////
        mineNum = new JTextField(4);
        timerField = new JTextField(4);
        mineNum.setText(""+leftFlag);
        mineNum.setEditable(false);
        timerField.setText(""+time);
        timerField.setEditable(false);
        upPanel.add(new JLabel("마인 갯수"));
        upPanel.add(mineNum);
        upPanel.add(new JLabel("시간"));
        upPanel.add(timerField);
        upPanel.setBackground(new Color(240, 240, 240));
        downPanel.setBackground(new Color(240, 240, 240));

        j = new Button[totalNum]; // 버튼 설정
        makeButton(1);
        makeTimer(); // 타이머 설정

        add("North",upPanel);    // 레이아웃 설정
        add("Center",downPanel);
        makeMenu();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(cols_ * 45, rows_ * 50);
        setVisible(true);
    }
    // 복사 생성자
    Mine_Sweeper(Mine_Sweeper ms) {
        rows_ = ms.rows_;
        cols_ = ms.cols_;
        totalNum = ms.totalNum;
        generNum = ms.generNum;
        numList = ms.numList;
        mine = ms.mine;
        leftFlag = ms.leftFlag;
        time = ms.time;
        clickedNum = ms.clickedNum;
        j = ms.j;
        upPanel = ms.upPanel;
        downPanel = ms.downPanel;
        mineNum = ms.mineNum;
        timerField = ms.timerField;
        setTitle("지뢰찾기");
        setLayout(new BorderLayout());

        makeButton(2);
        makeTimer();

        add("North",upPanel);
        add("Center",downPanel);
        makeMenu();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(cols_ * 45, rows_* 50);
        setVisible(true);
    }

    // 메뉴를 만들어주는 함수
    void makeMenu() {
        String saveDir = ".\\savefile.txt";
        JMenuBar mb = new JMenuBar();
        JMenu menu1 = new JMenu("게임");
        JMenu menu2 = new JMenu("파일");
        JMenu menu3 = new JMenu("도움말");

        mb.add(menu1);
        mb.add(menu2);
        mb.add(menu3);

        JMenuItem iStart = new JMenuItem("시작하기");
        JMenuItem iLevel = new JMenuItem("레벨 선택");
        JMenuItem iExit = new JMenuItem("종료하기");

        JMenuItem iSave = new JMenuItem("저장");
        JMenuItem iLoad = new JMenuItem("불러오기");

        JMenuItem iHelp = new JMenuItem("도움말");

        // 시작하기 버튼 이벤트 리스너
        iStart.addActionListener(e -> {
            new Mine_Sweeper(rows_,cols_);
            dispose();
        });

        // 레벨 선택 버튼 이벤트 리스너
        iLevel.addActionListener(e -> {
            new LevelFrame();
            dispose();
        });

        // 종료하기 버튼 이벤트 리스너
        iExit.addActionListener(e -> dispose());
        // 저장 버튼 이벤트 리스너
        iSave.addActionListener(e -> {
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveDir)))) {
                out.writeObject(this);
                JOptionPane.showMessageDialog(this, "정상적으로 저장되었습니다.",
                        "저장 완료!", JOptionPane.INFORMATION_MESSAGE);
                out.close();
            } catch (IOException err) {
                System.out.println("에러 메시지 : " + err.getMessage());
                JOptionPane.showMessageDialog(this, "저장에 실패하였습니다.",
                        "저장 실패!", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 불러오기 버튼 이벤트 리스터
        iLoad.addActionListener(e -> {
            try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(saveDir)))) {
                Mine_Sweeper copy = new Mine_Sweeper((Mine_Sweeper)in.readObject());
                dispose();
                JOptionPane.showMessageDialog(copy, "정상적으로 로드 되었습니다.",
                        "로드 완료!", JOptionPane.INFORMATION_MESSAGE);
                in.close();
            } catch (Exception err) {
                System.out.println("에러 메시지 : " + err.getMessage());
                JOptionPane.showMessageDialog(this, "로드에 실패하였습니다.",
                        "로드 실패!", JOptionPane.ERROR_MESSAGE);
            }

        });

        // 도움말 버튼 이벤트 리스너
        iHelp.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "지뢰를 안밟으면 됩니다.",
                    "간단해요.", JOptionPane.INFORMATION_MESSAGE);
        });
        menu1.add(iStart);
        menu1.add(iLevel);
        menu1.add(iExit);
        menu2.add(iSave);
        menu2.add(iLoad);
        menu3.add(iHelp);

        setJMenuBar(mb);
    }
    // 숫자를 중복없이 랜덤으로 생성해주는 함수
    int ranNumGenerator() {
        int number;

        do {
            number = (int) ((Math.random()) * totalNum + 1);
        }
        while (generNum[number] != 0);

        generNum[number]++;

        return number;
    }
    // 버튼에 기능 추가해주는 함수
    void makeButton(int n) {
        Mine_Sweeper frame = this;
        for (int i = 0; i < totalNum; i++) {  // 랜덤으로 생성한 숫자로 버튼을 생성
            final int num = i;
            if (n == 1) { // 새로하는 게임일 때
                numList[i] = ranNumGenerator();

                j[i] = new Button();
                if (numList[i] % 10 == 0) {     // 랜덤으로 마인 설정
                    j[i].setMine(true);
                }
                j[i].setBackground(new Color(110, 153, 204));

                j[i].setSize(20, 20);
                downPanel.add(j[i]);
            }

            j[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    /////////////////////////////////////////////
                    //////// 왼쪽 마우스 클릭 시 ////////////////
                    /////////////////////////////////////////////
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        int t = 0;
                        // 클릭한 버튼이 지뢰면 게임 종료
                        if (j[num].getMine()) {
                            for (Button btn : j) {
                                if (btn.getMine())
                                    btn.setBackground(new Color(206, 41, 57));
                            }
                            JOptionPane.showMessageDialog(frame, "패배 하였습니다~","이거밖에 안되시나요?",JOptionPane.QUESTION_MESSAGE);
                            dispose();
                            return;
                        }
                        // 클릭한 버튼이 꼭짓점일 때
                        if (num == 0) {
                            if (j[num + 1].getMine())
                                t++;
                            if (j[num + cols_].getMine())
                                t++;
                            if (j[num + cols_ + 1].getMine())
                                t++;
                        }
                        if (num == cols_ - 1) {
                            if (j[num - 1].getMine())
                                t++;
                            if (j[num + cols_ - 1].getMine())
                                t++;
                            if (j[num + cols_].getMine())
                                t++;
                        }
                        if (num == totalNum - cols_) {
                            if (j[num - cols_].getMine())
                                t++;
                            if (j[num - cols_ + 1].getMine())
                                t++;
                            if (j[num + 1].getMine())
                                t++;
                        }
                        if (num == totalNum - 1) {
                            if (j[num - cols_ - 1].getMine())
                                t++;
                            if (j[num - cols_].getMine())
                                t++;
                            if (j[num - 1].getMine())
                                t++;
                        }
                        if (num > 0 && num < cols_ - 1) { // 클릭한 버튼이 위쪽 모서리 일 때
                            if (j[num + 1].getMine())
                                t++;
                            if (j[num + cols_].getMine())
                                t++;
                            if (j[num + cols_ + 1].getMine())
                                t++;
                            if (j[num - 1].getMine())
                                t++;
                            if (j[num + cols_ - 1].getMine())
                                t++;
                        }
                        if (num >= cols_ && num < totalNum - cols_) { // 클릭한 버튼이 양 쪽 모서리일 때
                            if (num % cols_ == 0) {
                                if (j[num - cols_].getMine())
                                    t++;
                                if (j[num - cols_ + 1].getMine())
                                    t++;
                                if (j[num + 1].getMine())
                                    t++;
                                if (j[num + cols_].getMine())
                                    t++;
                                if (j[num + cols_ + 1].getMine())
                                    t++;
                            }
                            if (num % cols_ == cols_ - 1) {
                                if (j[num - cols_ - 1].getMine())
                                    t++;
                                if (j[num - cols_].getMine())
                                    t++;
                                if (j[num - 1].getMine())
                                    t++;
                                if (j[num + cols_ - 1].getMine())
                                    t++;
                                if (j[num + cols_].getMine())
                                    t++;
                            }
                        }
                        if (num > totalNum - cols_ && num < totalNum - 1) { // 아래쪽 모서리 일때
                            if (j[num - cols_ - 1].getMine())
                                t++;
                            if (j[num - cols_].getMine())
                                t++;
                            if (j[num - 1].getMine())
                                t++;
                            if (j[num - cols_ + 1].getMine())
                                t++;
                            if (j[num + 1].getMine())
                                t++;
                        }
                        if (num > cols_ && num < totalNum - cols_ - 1 &&
                                !(num % cols_ == 0 || num % cols_ == cols_ - 1)) {
                            if (j[num - cols_ - 1].getMine())
                                t++;
                            if (j[num - cols_].getMine())
                                t++;
                            if (j[num - 1].getMine())
                                t++;
                            if (j[num - cols_ + 1].getMine())
                                t++;
                            if (j[num + 1].getMine())
                                t++;
                            if (j[num + cols_].getMine())
                                t++;
                            if (j[num + cols_ + 1].getMine())
                                t++;
                            if (j[num + cols_ - 1].getMine())
                                t++;
                        }
                        j[num].setBackground(new Color(190, 190, 225));
                        if (!j[num].getClicked())
                            clickedNum++;
                        j[num].setClicked(true);
                        if (t != 0)
                            j[num].setText("" + t);

                        // 승리 조건
                        if (clickedNum == totalNum - mine) {
                            JOptionPane.showMessageDialog(frame, "좀 하시네요?","축하합니다!",JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                    }

                    /////////////////////////////////////////////
                    //////// 오른쪽 마우스 클릭 시 //////////////
                    /////////////////////////////////////////////
                    if (SwingUtilities.isRightMouseButton(e) && j[num].getFlag()) { // 만약 이미 깃발이 세워져 있다면
                        j[num].setBackground(new Color(110, 153, 204));
                        j[num].setFlag(false);
                        leftFlag++;
                        mineNum.setText("" + leftFlag);
                        return;
                    }
                    if (SwingUtilities.isRightMouseButton(e) && !j[num].getClicked()) {
                        j[num].setBackground(new Color(1, 32, 201));
                        j[num].setFlag(true);
                        leftFlag--;
                        mineNum.setText("" + leftFlag);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }
    }
    // 타이머 함수
    void makeTimer() {
        Timer timer = new Timer(1000, e -> {
            time++;
            timerField.setText(" "+time+" ");
        });
        timer.start();
    }

    public static void main(String[] args) throws IOException {
        Mine_Sweeper p = new Mine_Sweeper(10,10);
    }
}

// 버튼 구현
class Button extends JButton implements Serializable{
    private boolean isClicked;
    private boolean isMine;
    private boolean isFlag;
    Button() {
        super();
    }

    void setClicked(boolean bool) {
        isClicked = bool;
        isFlag = false;
    }
    void setFlag(boolean bool) { isFlag = bool; }
    void setMine(boolean bool) { isMine = bool; }

    boolean getMine() { return isMine; }
    boolean getClicked() { return isClicked; }
    boolean getFlag() { return isFlag; }
}
// 레벨 선택 프레임
class LevelFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    LevelFrame() {
        setTitle("레벨 선택");

        JPanel pan = new JPanel(new GridLayout(5,0));
        pan.setBackground(new Color(190,190,225));
        pan.add(new JLabel("난이도 선택"));
        ButtonGroup lev = new ButtonGroup();
        JRadioButton easy = new JRadioButton("초급");
        easy.setActionCommand("easy");
        JRadioButton middle = new JRadioButton("중급");
        middle.setActionCommand("middle");
        JRadioButton hard = new JRadioButton("고급");
        hard.setActionCommand("hard");
        JButton selectButton = new JButton("선택");

        selectButton.addActionListener(e -> {
            if (lev.getSelection().getActionCommand().equals("easy")) {
                new Mine_Sweeper(10,10);
                dispose();
            }
            else if (lev.getSelection().getActionCommand().equals("middle")) {
                new Mine_Sweeper(10,20);
                dispose();
            }
            else {
                new Mine_Sweeper(20,30);
                dispose();
            }
        });
        easy.setSelected(true);

        lev.add(easy);
        lev.add(middle);
        lev.add(hard);

        pan.add(easy);
        pan.add(middle);
        pan.add(hard);
        pan.add(selectButton);

        add(pan,BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 150);
        setVisible(true);
    }
}