package br.com.ws.simpleEnv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class Service {

	private static final String CAMINHO_WORKSPACE_STANDARD = "CAMINHO_WORKSPACE_STANDARD";
	private static final String CAMINHO_MAVEN_SETTINGS = "CAMINHO_MAVEN_SETTINGS";
	private static final String CAMINHO_FONTES = "CAMINHO_FONTES";

	private static final String WORKSPACE = "workspace";
	private static final String SETTINGS_XML = "settings.xml";
	private static final String MAVEN = "maven";
	private static final String SIM3G = "sim3g";

	private static final String LOCAL_REPOSITORY = "localRepository";

	private Properties props;

	public Service(Properties props) {
		this.props = props;
	}

	private static Properties getProperties() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		InputStream is = new FileInputStream("config.properties");
		props.load(is);
		return props;
	}

	private void constroiAmbiente() throws FileNotFoundException, IOException {
		File diretorio = new File(props.getProperty(CAMINHO_FONTES));
		File[] listFiles = diretorio.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			if (file.isDirectory()) {
				criaDiretorio(file.getAbsolutePath(), SIM3G);
				criaDiretorioWorkspace(file);
				copiaArquivoMaven(criaDiretorio(file.getAbsolutePath(), MAVEN));
			}
		}
	}

	private void criaDiretorioWorkspace(File file) throws IOException {
		File workspace = new File(file.getAbsolutePath(), WORKSPACE);
		if (!workspace.exists())
			copyDirectory(new File(props.getProperty(CAMINHO_WORKSPACE_STANDARD)), workspace);
	}

	private File criaDiretorio(String caminho, String nome) {
		File file = new File(caminho, nome);
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}

	private void copiaArquivoMaven(File maven)
			throws FileNotFoundException, IOException {
		File settings = new File(maven.getAbsolutePath(), SETTINGS_XML);

		if (!settings.exists()) {

			InputStream fis = new FileInputStream(props.getProperty(CAMINHO_MAVEN_SETTINGS));
			Reader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			OutputStream fos = new FileOutputStream(settings);
			Writer osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);

			String linha = br.readLine();
			while (linha != null) {
				if (linha.contains(LOCAL_REPOSITORY)) {
					linha = linha.replaceAll("\\>(.*)\\<", ">" + maven.getAbsolutePath() + "<");
				}
				bw.write(linha);
				bw.newLine();
				linha = br.readLine();

			}
			br.close();
			bw.close();
		}
	}

	private void copyDirectory(File srcDir, File dstDir) throws IOException {
		if (srcDir.isDirectory()) {
			if (!dstDir.exists()) {
				dstDir.mkdir();
			}
			String[] children = srcDir.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
			}
		} else {
			copy(srcDir, dstDir);
		}
	}

	private void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
	public static void main(String[] args) throws IOException {

		Properties props = getProperties();
		Service servico = new Service(props);
		servico.constroiAmbiente();

	}

}
