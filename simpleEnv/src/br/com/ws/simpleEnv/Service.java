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
import java.util.Scanner;

public class Service {

	private static final String SETTINGS_CHANGE = "S";
	private static final String AMBIENTE_CREATE = "A";
	private static final String WINDOWS_OS = "Windows";
	private static final String JA_EXISTE = "Ja existe, por isso nao sera criado!";
	private static final String SIMPLE_ENV_LOG = "> [SIMPLE_ENV] ";
	private static final String CAMINHO_WORKSPACE_STANDARD = "CAMINHO_WORKSPACE_STANDARD";
	private static final String CAMINHO_MAVEN_SETTINGS = "CAMINHO_MAVEN_SETTINGS";
	private static final String CAMINHO_FONTES = "CAMINHO_FONTES";

	private static final String WORKSPACE = "workspace";
	private static final String SETTINGS_XML = "settings.xml";
	private static final String MAVEN = "maven";
	private static final String SIM3G = "sim3g";

	private static final String LOCAL_REPOSITORY = "localRepository";
	private static final String CAMINHO_LIB_MAVEN_CONFIG = "CAMINHO_LIB_MAVEN_CONFIG";
	private static final String SVN = "SVN";
	private static final String GIT = "GIT";
	private static final String GIT_SETTINGS = "settings_git.xml";
	private static final String SVN_SETTINGS = "settings_svn.xml";
	private static final String CONFIG = "config.properties";

	private Properties props;

	public Service(Properties props) {
		this.props = props;
	}

	private static Properties getProperties() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		File config = new File(CONFIG);
		InputStream is = new FileInputStream(config);
		props.load(is);
		return props;
	}

	private void constroiAmbiente() throws FileNotFoundException, IOException {
		System.out.println("Construindo ambiente!");
		File diretorio = new File(props.getProperty(CAMINHO_FONTES));
		File[] listFiles = diretorio.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			if (file.isDirectory()) {
				log("|-------------------------------------------------------------------------------|");
				criaDiretorioWorkspace(file);
				criaDiretorio(file.getAbsolutePath(), SIM3G);
				copiaArquivoMaven(criaDiretorio(file.getAbsolutePath(), MAVEN));
				log("|-------------------------------------------------------------------------------|");
			}
		}
		log("Ambiente construido!");
	}

	private void criaDiretorioWorkspace(File file) throws IOException {
		log("Criando workspace " + file);
		File workspace = new File(file.getAbsolutePath(), WORKSPACE);
		if (!workspace.exists()) {
			log("Workspace criado !");
			copyDirectory(new File(props.getProperty(CAMINHO_WORKSPACE_STANDARD)), workspace);
		} else {
			log(WORKSPACE + JA_EXISTE);
		}
	}

	private File criaDiretorio(String caminho, String nome) {
		log("Criando diretorio " + nome);
		File file = new File(caminho, nome);
		if (!file.exists()) {
			file.mkdir();
			log("Diret�rio criado: " + nome);
		} else {
			log(nome + JA_EXISTE);
		}
		return file;
	}

	private void copiaArquivoMaven(File maven) throws FileNotFoundException, IOException {
		log("Copiando arquivo maven settings " + maven);
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
					String mavenPath;
					if(isWindows()) {
						mavenPath = maven.getAbsolutePath().replace("\\", "\\\\");
					} else {
						mavenPath = maven.getAbsolutePath();
					}					
					linha = linha.replaceAll("\\>(.*)\\<", ">" + mavenPath + "<");
				}
				bw.write(linha);
				bw.newLine();
				linha = br.readLine();

			}
			br.close();
			bw.close();
		} else {
			log("Settings ja existe! por isso nao sera copiado!");
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
		if(args.length > 0) {
			String input = args[0];
			if(input.equalsIgnoreCase(AMBIENTE_CREATE)) {
				servico.constroiAmbiente();
			} else if(isSVN(input) || isGIT(input)) {
				servico.mudaSettings(input);
			}
		}else {

			boolean stillRunning= true;	
			Scanner scan = new Scanner(System.in);
			while (stillRunning) {

				log("Deseja criar um novo ambiente de configuracao para o SVN  ou alterar config do settings? AMBIENTE(A), SETTINGS(S) ou EXIT para sair.");
				String input = scan.next();
			
				if (input.equalsIgnoreCase("exit")) {				
					scan.close();
					stillRunning = false;
				}
				if (input.toUpperCase().equals(AMBIENTE_CREATE)) {
					servico.constroiAmbiente();
				} else if(input.toUpperCase().equals(SETTINGS_CHANGE)) {
					servico.alteraSettings(scan);
				} else {
					stillRunning = false;
					log("Input informado nao encontrado!");
				}
			}
			
			scan.close();
		}
		

	}
	private void alteraSettings(Scanner scan) throws IOException {
		log("Alterar para SVN ou para GIT ?");
		String input = scan.next();
		mudaSettings(input);
	}

	private void mudaSettings(String input) throws IOException {
		if(isSVN(input)){
			File settingsSVN = new File(SVN_SETTINGS);
			copy(settingsSVN, getCaminhoSettingMaven());
			log("alterado para settings SVN");

		} else if(isGIT(input)) {
			File settingsGit = new File(GIT_SETTINGS);
			copy(settingsGit, getCaminhoSettingMaven());
			log("alterado para settings GIT");
		} else {
			log("Input informado nao existe!");
		}
	}

	private static boolean isGIT(String input) {
		return input.toUpperCase().equals(GIT);
	}

	private static boolean isSVN(String input) {
		return input.toUpperCase().equals(SVN);
	}

	private File getCaminhoSettingMaven() {
		return new File(props.getProperty(CAMINHO_LIB_MAVEN_CONFIG), SETTINGS_XML);
	}

	private static void log(String msg) {
		System.out.println(SIMPLE_ENV_LOG + msg);
	}
	private static boolean isWindows() {
		return System.getProperty("os.name").contains(WINDOWS_OS);
	}
}
