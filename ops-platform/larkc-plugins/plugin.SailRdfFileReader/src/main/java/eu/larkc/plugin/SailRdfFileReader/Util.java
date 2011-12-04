package eu.larkc.plugin.SailRdfFileReader;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

public class Util {

	public static void copyFileOrDirectory(FileSystem srcFS, Path src, FileSystem destFS, Path dest, Configuration config) throws IOException {
		if (srcFS.getFileStatus(src).isDir()) {
			destFS.mkdirs(dest);
			for (FileStatus f: srcFS.listStatus(src)) {
				copyFileOrDirectory(srcFS, f.getPath(), destFS, new Path(dest,f.getPath().getName()),config);
			}
		} else
			FileUtil.copy( srcFS,  src,  destFS,  dest, false, config);
	}
}
