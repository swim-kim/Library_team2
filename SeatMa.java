import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SeatMa extends JFrame {

    private Map<JButton, CustomButton> seatMap;
    private JButton logoutButton;
    private JButton selectedButton;
    private JLabel idLabel;
    private JLabel timeLabel;
    private JLabel remainingTimeLabel;
    private String ID;
    private String selectedSeat;

    public SeatMa(String ID) {
        super("Seat Management System");
        this.ID = ID;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 1000);

        // Frame에 GridLayout 적용
        GridLayout gridLayout = new GridLayout(3, 8);
        gridLayout.setHgap(50); // 열 사이의 가로 간격 설정
        gridLayout.setVgap(50); // 행 사이의 세로 간격 설정
        setLayout(gridLayout);

        seatMap = new HashMap<>();
        selectedButton = null;

        // 각 공간에 버튼을 추가한 JPanel 생성하여 Frame에 추가
        for (int i = 1; i < 25; i++) {
            addSeatButtons((i * 10 - 9), i * 10, 2, 100);
        }
        idLabel = new JLabel("ID:  | 좌석: ");
        timeLabel = new JLabel("사용 시간: ");
        remainingTimeLabel = new JLabel("남은 시간: ");

        // ID, 좌석, 사용시간 추가
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(idLabel);
        infoPanel.add(timeLabel);
        infoPanel.add(remainingTimeLabel);

        add(infoPanel, BorderLayout.NORTH);

        // 로그아웃 버튼 추가
        JPanel logoutPanel = new JPanel();
        logoutPanel.setPreferredSize(new Dimension(150, 50)); // 로그아웃 패널 크기 설정

        logoutButton = new JButton("로그아웃");
        logoutButton.setPreferredSize(new Dimension(150, 50));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Logout.logout(); // Logout 클래스의 logout 메서드 호출
            }
        });
        logoutPanel.add(logoutButton);
        add(logoutPanel);

        setVisible(true);

    }

    public SeatMa() {
    }

    // 범위에 해당하는 버튼을 추가하는 메소드
    private void addSeatButtons(int start, int end, int columns, int height) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.GRAY);

        // GridLayout으로 버튼을 2열로 배치
        GridLayout gridLayout = new GridLayout(0, columns);
        panel.setLayout(gridLayout);
        gridLayout.setHgap(10); // 버튼 사이의 가로 간격 설정
        gridLayout.setVgap(10); // 버튼 사이의 세로 간격 설정

        // 버튼 추가
        for (int i = start; i <= end; i++) {
            JButton button = new JButton("" + i);
            button.setFont(button.getFont().deriveFont(Font.PLAIN, 11)); // 버튼의 폰트 크기 조절
            button.setMargin(new Insets(10, 10, 10, 10)); // 버튼의 여백 조절
            button.setBackground(Color.WHITE); // 버튼의 배경색 설정

            // 시간바로 사용할 커스텀 버튼 생성
            Calendar expirationDate = Calendar.getInstance();
            expirationDate.add(Calendar.DAY_OF_MONTH, i);
            CustomButton customButton = new CustomButton(i, button, expirationDate.getTime());

            button.add(customButton); // 버튼에 시간바 추가

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectedButton == null) {
                        int choice = JOptionPane.showConfirmDialog(button, "좌석을 선택하시겠습니까?", "좌석 선택",
                                JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            int selectedTime = Integer
                                    .parseInt(JOptionPane.showInputDialog(button, "사용 시간을 입력하세요(분):"));
                            if (selectedTime <= 0) {
                                JOptionPane.showMessageDialog(button, "올바른 시간을 입력하세요.", "오류",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                button.setBackground(Color.YELLOW); // 버튼 색상 변경
                                customButton.startTimer(selectedTime); // 시간바 타이머 시작
                                selectedButton = button;
                                selectedSeat = button.getText();
                                idLabel.setText("ID: " + ID + " | 좌석: " + selectedSeat);
                                timeLabel.setText("사용 시간: " + selectedTime + "분");
                            }
                        }
                    } else if (selectedButton == button) {
                        int choice = JOptionPane.showConfirmDialog(button, "퇴실하시겠습니까?", "퇴실 확인",
                                JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            button.setBackground(Color.WHITE); // 버튼 색상 초기화
                            customButton.stopTimer(); // 시간바 타이머 중지
                            selectedButton = null;
                            idLabel.setText("ID: | 좌석: ");
                            timeLabel.setText("사용 시간: ");
                            remainingTimeLabel.setText("남은 시간: ");
                        }
                    } else {
                        JOptionPane.showMessageDialog(button, "이미 좌석을 선택하셨습니다.", "경고",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            seatMap.put(button, customButton);
            panel.add(button);
        }

        // 패널의 크기 설정
        Dimension panelSize = new Dimension(panel.getPreferredSize().width, height);
        panel.setPreferredSize(panelSize);

        add(panel);

    }

    // 커스텀 버튼 클래스
    private class CustomButton extends JPanel {
        private JLabel label;
        private JProgressBar progressBar;
        private Timer timer;
        private int remainingTime;
        private JButton button;
        private Date expirationDate;

        public CustomButton(int number, JButton button, Date expirationDate) {
            setLayout(new BorderLayout());
            this.button = button;
            this.expirationDate = expirationDate;

            label = new JLabel("" + number, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 16)); // 레이블의 폰트 설정
            add(label, BorderLayout.NORTH);

            progressBar = new JProgressBar(0, 5 * 60); // 5분을 초 단위로 설정
            progressBar.setStringPainted(false); // 바에 텍스트 표시하지 않음
            progressBar.setPreferredSize(new Dimension(getWidth(), 10)); // 바의 크기 설정
            progressBar.setBackground(new Color(0, 0, 0, 0)); // 바의 배경색을 투명으로 설정
            progressBar.setVisible(false); // 타이머를 처음에는 숨김
            add(progressBar, BorderLayout.CENTER);
        }

        public void startTimer(int selectedTime) {
            remainingTime = selectedTime * 60;

            progressBar.setValue(remainingTime);
            progressBar.setVisible(true);

            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { // 시간에 따라 백그라운드색 바꾸기
                    remainingTime--;

                    if (remainingTime <= 0) { // 잔여시간이 0이되면 자동으로 퇴실
                        timer.stop(); // Stop the timer
                        button.setBackground(Color.WHITE);
                        progressBar.setVisible(false);
                        progressBar.setValue(5 * 60);
                        idLabel.setText("ID: | 좌석: ");
                        timeLabel.setText("사용 시간: ");
                        remainingTimeLabel.setText("남은 시간: ");
                        JOptionPane.showMessageDialog(button, "퇴실되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        progressBar.setValue(remainingTime);

                        if (remainingTime <= 30 * 60) { // 30분 이하로 남았을때 빨간색으로 바뀜
                            progressBar.setBackground(Color.RED);
                            button.setBackground(Color.RED);
                        } else {
                            progressBar.setBackground(Color.YELLOW);
                            button.setBackground(Color.YELLOW);
                        }
                    }
                    int minutes = remainingTime / 60;
                    int seconds = remainingTime % 60;
                    String remainingTimeString = String.format("%02d:%02d", minutes, seconds);
                    remainingTimeLabel.setText("남은 시간: " + remainingTimeString);
                }
            });

            timer.start(); // Start the timer
        }

        public void stopTimer() {
            if (timer != null && timer.isRunning()) {
                timer.stop(); // 타이머 중지
            }
            progressBar.setVisible(false); // 바 숨김
            progressBar.setValue(5 * 60); // 바 초기화
            remainingTimeLabel.setText("남은 시간: ");
        }
    }

    // public static void main(String[] args) {
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // new SeatMa();
    // }
    // });
    // }
}