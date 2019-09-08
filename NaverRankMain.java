import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.*;

class TimeRank
{
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	LocalDateTime time = LocalDateTime.now(ZoneId.of("GMT+9")).withNano(0);
	String timeString = time.format( formatter );
	String timeSimple = time.format( DateTimeFormatter.ISO_LOCAL_TIME );
	int rank = 0;

	public void TimeRank(){};
	public void TimeRank(int rank){
		time = LocalDateTime.now();
		this.rank = rank;
	}

	public String[] fetchRanking() throws IOException, Exception{
		URL naverHome = new URL("https://www.naver.com");
		HttpURLConnection conn = (HttpURLConnection) naverHome.openConnection();
		InputStream in = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(in, "UTF-8");
		BufferedReader bfr = new BufferedReader(isr);

		String readString = "";
		String rankbefore_preString = "<span class=\"ah_r\">";
		String rankbefore_postString = "</span>";
		String rankString = "";
		String [] rank1to10 = new String[10];
		int end = 0;

		for (int i=1; i<=10; i++){
			rankString = rankbefore_preString + i + rankbefore_postString;
			while ((readString = bfr.readLine()) != null){
				if (readString.equals(rankString)){
					rankString = bfr.readLine();
					break;
				}
			}
			end = rankString.indexOf("</span>");
			rankString = rankString.substring(19, end);
			rank1to10[i-1] = rankString;
		}
		return rank1to10;
	}

	public String addNewTimeRank(String [] timerank){
		time = LocalDateTime.now(ZoneId.of("GMT+9")).withNano(0);
		timeSimple = time.format( DateTimeFormatter.ISO_LOCAL_TIME );
		String result = timeSimple + "\t";
		for (int i=0; i<10; i++){
			// if (timerank[i].length() > 3)
			// 	timerank[i] = timerank[i].substring(0, 3);
			result += timerank[i] + "\t";
		}
		return result + "\r\n";
	}
}


public class NaverRankMain
{
	public static void main(String[] args) throws IOException, Exception
	{
		TimeRank temp = new TimeRank();
		//TimeRank [] timerankArr = new TimeRank[120];
		String[] rankarr = temp.fetchRanking();
		String newTimeRank = "";

		String intro = "\t----- Naver Top 10 Trending Searches (" + temp.timeString + " KST) -----\r\n\t\t1st\t2nd\t3rd\t4th\t5th\t6th\t7th\t8th\t9th\t10th\r\n";

		for (int i=0; i<10; i++){
			if (i==0) System.out.println(temp.timeSimple + "Rank");
			System.out.println("  " + (i+1) + ": " + rankarr[i]);
		}
		newTimeRank = temp.addNewTimeRank(rankarr);
		System.out.println( "\r\n" + intro + newTimeRank );

		File ranktxt = new File("/Users/dhryou/Documents", "NaverRank.txt");
		FileOutputStream fos = new FileOutputStream( ranktxt );
		PrintWriter pwriter = new PrintWriter( fos );
		StringBuffer add = new StringBuffer( newTimeRank );
		pwriter.write( intro );
		pwriter.append( add );
		pwriter.flush();

		int freq = 0;
		Thread main = Thread.currentThread();
		while (freq++ < 10){
			main.sleep(5000);
			rankarr = temp.fetchRanking();
			newTimeRank = temp.addNewTimeRank(rankarr);
			System.out.println(newTimeRank);
			add = new StringBuffer(newTimeRank);
			pwriter.append( add );
			pwriter.flush();
		}

		fos.close();
	}
}