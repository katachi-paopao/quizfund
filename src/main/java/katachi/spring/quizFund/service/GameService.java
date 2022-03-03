package katachi.spring.quizFund.service;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import katachi.spring.quizFund.model.Quizset;
import katachi.spring.quizFund.model.game.Game;

@Service
public class GameService {
	@Autowired
	QuizService qService;
	public Optional<Game> initGame(int qsid) {
		Optional<Quizset> o_qs = qService.findOpenQuizsetByid(qsid);
		if (o_qs.isPresent()) {
			Quizset qs = o_qs.get();
			if (qs.isRandom()) {
				Collections.shuffle(qs.getQuizList());
			}
			return Optional.of(new Game(qs.getId(), qs.getQuizList(), qService));
		} else {
			return Optional.empty();
		}
	}

//	public static String createImage() throws IOException {
//		BufferedImage img = new BufferedImage(480, 320, BufferedImage.TYPE_3BYTE_BGR);
//		final Font f = new Font("メイリオ", Font.PLAIN, 32);
//
//		Graphics g = img.getGraphics();
//        g.setColor(new Color(64, 128, 64));
//        g.fillRect(0, 0, 480, 320);
//        g.setColor(Color.WHITE);
//        g.setFont(f);
//        g.drawString("abcdefg", 120, 120);
//        g.dispose();
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write( img, "png", baos );
//        baos.flush();
//        byte[] res = baos.toByteArray();
//        baos.close();
//
//        Base64 base64 = new Base64();
//        return new String(base64.encode(res));
//	}
}
