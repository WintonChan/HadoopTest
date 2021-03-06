package hadoop.test.pagerank;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.JobConf;

public class PageRankJob {
	public static final String HDFS = "hdfs://125.216.242.37:9000";
	public static final Pattern DELIMITER = Pattern.compile("[\t,]");

	public static void main(String[] args) {
		Map<String, String> path = new HashMap<String, String>();
		path.put("page", "data/page.csv");// 本地的数据文件
		path.put("pr", "data/pr.csv");// 本地的数据文件

		path.put("input", HDFS + "/user/root/test/pagerank");// HDFS的目录
		path.put("input_pr", HDFS + "/user/root/test/pagerank/pr");// pr存储目
		path.put("tmp1", HDFS + "/user/root/test/pagerank/tmp1");// 临时目录,存放邻接矩阵
		path.put("tmp2", HDFS + "/user/root/test/pagerank/tmp2");// 临时目录,计算到得PR,覆盖input_pr

		path.put("result", HDFS + "/user/root/test/pagerank/result");// 计算结果的PR

		try {

			AdjacencyMatrix.run(path);
			int iter = 3;
			for (int i = 0; i < iter; i++) {// 迭代执行
				PageRank.run(path);
			}
			Normal.run(path);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static JobConf config() {// Hadoop集群的远程配置信息
		JobConf conf = new JobConf(PageRankJob.class);
		conf.setJobName("PageRank");
		conf.addResource("classpath:/hadoop/core-site.xml");
		conf.addResource("classpath:/hadoop/hdfs-site.xml");
		conf.addResource("classpath:/hadoop/mapred-site.xml");
		return conf;
	}

	public static String scaleFloat(float f) {// 保留6位小数
		DecimalFormat df = new DecimalFormat("##0.000000");
		return df.format(f);
	}
}
