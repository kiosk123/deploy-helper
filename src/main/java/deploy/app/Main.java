package deploy.app;



import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Main {
    public static void main(String[] args) {

        if (args.length < 6) {
            System.out.println("help :: java -jar deploy-helper.jar <host> <port> <user> <password> <localFilePath> <remoteFilePath>");
            return;
        }

        String host = args[0]; // 서버의 IP 주소 또는 도메인
        int port = Integer.parseInt(args[1]);
        String user = args[2]; // SSH 로그인에 사용할 유저 이름
        String password = args[3]; // 비밀번호
        String localFilePath = args[4]; // 로컬 파일 경로
        String remoteFilePath = args[5]; // 원격 서버의 파일 경로

        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            String msg = String.format("send %s to server : %s:%d - %s", localFilePath, host, port, remoteFilePath);
            System.out.println(msg);
            // 1. SSH 세션 생성 및 설정
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);

            // 2. 세션 및 채널 연결
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("connect success...");

            // 3. SFTP 채널 사용
            channelSftp = (ChannelSftp) channel;
            channelSftp.put(localFilePath, remoteFilePath); // 파일 전송

            msg = String.format("File transfer %s to %s completed.", localFilePath, remoteFilePath);
            System.out.println(msg);
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            // 4. 자원 정리
            if (channelSftp != null) {
                channelSftp.exit();
            }
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
