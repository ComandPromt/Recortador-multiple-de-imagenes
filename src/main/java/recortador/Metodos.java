package recortador;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Metodos {

	public static void conversion(String extension, String salida, String carpeta) {

		LinkedList<String> listaImagenes = directorio(carpeta, extension, true, false);

		int resto = 3;

		if (extension.length() == 4) {
			resto = 5;
		}

		for (int i = 0; i < listaImagenes.size(); i++) {

			File f1 = new File(carpeta + Main.getSeparador() + listaImagenes.get(i));

			File f2 = new File(carpeta + Main.getSeparador()
					+ listaImagenes.get(i).substring(0, listaImagenes.get(i).length() - resto) + "." + salida);

			f1.renameTo(f2);

		}

		listaImagenes.clear();
	}

	public static void convertir(String carpeta) {

		conversion("jpeg", "jpg", carpeta);

		conversion("JPEG", "jpg", carpeta);

		conversion("JPG", "jpg", carpeta);

		conversion("PNG", "png", carpeta);

		conversion("GIF", "gif", carpeta);

	}

	public static String getSHA256Checksum(String filename) {

		String result = "";

		try {

			byte[] b;

			b = createChecksum(filename);

			StringBuilder bld = new StringBuilder();

			for (int i = 0; i < b.length; i++) {
				bld.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
			}

			result = bld.toString();

		} catch (Exception e) {
			//
		}

		return result;
	}

	public static byte[] createChecksum(String filename) throws NoSuchAlgorithmException, IOException {

		InputStream fis = null;

		MessageDigest complete = MessageDigest.getInstance("SHA-256");

		try {

			fis = new FileInputStream(filename);

			byte[] buffer = new byte[1024];

			int numRead;

			do {

				numRead = fis.read(buffer);

				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}

			}

			while (numRead != -1);

			fis.close();

		}

		catch (IOException e) {

			if (fis != null) {
				fis.close();
			}

		}

		return complete.digest();
	}

	public static List<String> borrarArchivosDuplicados(String directorio) {

		LinkedList<String> listaImagenes = directorio(directorio, ".", true, false);

		LinkedList<String> listaImagenesSha = new LinkedList<String>();

		for (int i = 0; i < listaImagenes.size(); i++) {
			listaImagenesSha.add(getSHA256Checksum(directorio + Main.getSeparador() + listaImagenes.get(i)));
		}

		List<String> duplicateList = listaImagenesSha.stream()

				.collect(Collectors.groupingBy(s -> s)).entrySet().stream()

				.filter(e -> e.getValue().size() > 1).map(e -> e.getKey()).collect(Collectors.toList());

		int indice = 0;

		for (String archivoRepetido : duplicateList) {

			for (int i = 0; i < Collections.frequency(listaImagenesSha, archivoRepetido) - 1; i++) {

				indice = listaImagenesSha.indexOf(archivoRepetido);

				eliminarFichero(directorio + listaImagenes.get(indice));

				listaImagenes.remove(indice);
			}

		}

		return listaImagenes;
	}

	public static LinkedList<String> directorio(String ruta, String extension, boolean filtro, boolean carpeta) {

		LinkedList<String> lista = new LinkedList<String>();

		File f = new File(ruta);

		if (f.exists()) {

			File[] ficheros = f.listFiles();

			String fichero = "";

			String extensionArchivo;

			File folder;

			for (int x = 0; x < ficheros.length; x++) {

				fichero = ficheros[x].getName();

				folder = new File(ruta + fichero);

				if (filtro) {

					if (folder.isFile()) {

						extensionArchivo = extraerExtension(fichero);

						if (fichero.length() > 5 && fichero.substring(0, fichero.length() - 5).contains(".")) {

							renombrar(ruta + fichero, ruta + eliminarPuntos(fichero));

						}

						if (extension.equals("webp") && extensionArchivo.equals("webp")
								|| extension.equals("jpeg") && extensionArchivo.equals("jpeg") || extension.equals(".")
								|| extension.equals(extensionArchivo)) {

							if (carpeta) {
								lista.add(ruta + fichero);
							}

							else {
								lista.add(fichero);
							}

						}

					}

				}

				else {

					if (folder.isDirectory()) {

						if (carpeta) {
							lista.add(ruta + fichero);
						}

						else {

							fichero = fichero.trim();

							if (!fichero.isEmpty()) {
								lista.add(fichero);
							}

						}

					}

				}

			}

		}

		return lista;

	}

	public static String eliminarEspacios(String cadena) {

		cadena = cadena.trim();

		cadena = cadena.replace("  ", " ");

		cadena = cadena.trim();

		return cadena;
	}

	public static LinkedList<String> directorio(String ruta, String extension, int filtro) {

		LinkedList<String> lista = new LinkedList<>();

		File f = new File(ruta);

		if (f.exists()) {

			lista.clear();

			File[] ficheros = f.listFiles();

			String fichero = "";

			String extensionArchivo;

			File folder;

			for (int x = 0; x < ficheros.length; x++) {

				fichero = ficheros[x].getName();

				folder = new File(ruta + fichero);

				if (filtro == 1) {

					if (folder.isFile()) {

						extensionArchivo = extraerExtension(fichero);

						if (fichero.length() > 5 && fichero.substring(0, fichero.length() - 5).contains(".")) {

							renombrar(ruta + fichero, ruta + eliminarPuntos(fichero));

						}

						if (extension.equals("webp") && extensionArchivo.equals("webp")
								|| extension.equals("jpeg") && extensionArchivo.equals("jpeg") || extension.equals(".")
								|| extension.equals(extensionArchivo)) {

							lista.add(fichero);
						}

					}

				}

				else {

					if (folder.isDirectory()) {
						lista.add(fichero);
					}

				}

			}

		}

		return lista;

	}

	public static JSONObject apiImagenes(String parametros) throws IOException {

		JSONObject json = readJsonFromUrl("https://apiperiquito.herokuapp.com/recibo-json.php?imagenes=" + parametros);

		return json;

	}

	public static JSONObject readJsonFromUrl(String url) throws IOException {

		InputStream is = new URL(url).openStream();

		BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

		String jsonText = readAll(rd);
		is.close();

		return new JSONObject(jsonText);

	}

	private static String readAll(Reader rd) throws IOException {

		StringBuilder sb = new StringBuilder();

		int cp;

		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}

		return sb.toString();
	}

	public static String obtenerParametros(List<String> list) {

		StringBuilder bld = new StringBuilder();
		String extension;

		for (int i = 0; i < list.size(); i++) {

			extension = list.get(i).substring(list.get(i).length() - 3, list.get(i).length());

			if (list.size() == 1 || i + 1 == list.size()) {
				bld.append(i + "." + extension);

			}

			else {

				bld.append(i + "." + extension + ",");
			}

		}

		return bld.toString();
	}

	public static void renombrarArchivos(String ruta, String filtro, boolean api) throws IOException {

		List<String> list = directorio(ruta, filtro, 1);

		if (list.size() > 0) {

			File f1;

			File f2;

			File f3;

			JSONArray imagenesBD = null;

			if (api) {

				JSONObject json;

				String parametros = Metodos.obtenerParametros(list);

				json = Metodos.apiImagenes(parametros);

				imagenesBD = json.getJSONArray("imagenes_bd");
			}

			for (int x = 0; x < list.size(); x++) {

				f1 = new File(ruta + list.get(x));

				f2 = new File(ruta + Metodos.eliminarPuntos(list.get(x)));

				if (f1.isFile() && f1.renameTo(f2)) {

					if (api) {

						f3 = new File(ruta + imagenesBD.get(x));

						if (!f2.renameTo(f3)) {
							x = list.size();
						}

					}

				}

				else {

					x = list.size();

				}

			}
		}
	}

	public static String extraerExtension(String nombreArchivo) {

		String extension = "";

		if (nombreArchivo.length() >= 3) {

			extension = nombreArchivo.substring(nombreArchivo.length() - 3, nombreArchivo.length());

			extension = extension.toLowerCase();

			if (extension.equals("peg")) {
				extension = "jpeg";
			}

			if (extension.equals("ebp")) {
				extension = "webp";
			}

		}

		return extension;
	}

	public static void renombrar(String ruta1, String ruta2) {

		File f1 = new File(ruta1);

		File f2 = new File(ruta2);

		f1.renameTo(f2);

	}

	public static LinkedList<String> directorio(String ruta, String extension) {

		LinkedList<String> lista = new LinkedList<>();

		File f = new File(ruta);

		if (f.exists()) {

			lista.clear();

			File[] ficheros = f.listFiles();

			String fichero = "";

			String extensionArchivo;

			File folder;

			boolean comprobacion;

			for (int x = 0; x < ficheros.length; x++) {

				fichero = ficheros[x].getName();

				folder = new File(ruta + "/" + fichero);

				extensionArchivo = extraerExtension(fichero);

				comprobacion = !folder.isDirectory();

				if (comprobacion && fichero.length() > 5 && fichero.substring(0, fichero.length() - 5).contains(".")) {

					renombrar(ruta + "/" + fichero, ruta + "/" + eliminarPuntos(fichero));

				}

				if (comprobacion && extension.equals("webp") && extensionArchivo.equals("webp")
						|| comprobacion && extension.equals("jpeg") && extensionArchivo.equals("jpeg")
						|| comprobacion && extension.equals(".")
						|| comprobacion && extension.equals(extensionArchivo)) {

					lista.add(fichero);
				}

			}
		}

		return lista;

	}

	public static String saberSeparador(String os) {

		if (os.equals("Linux")) {
			return "/";
		}

		else {
			return "\\";
		}

	}

	public static String eliminarPuntos(String cadena) {

		String cadena2 = cadena.substring(0, cadena.length() - 4);

		cadena = cadena2.replace(".", "_") + "." + extraerExtension(cadena);

		return cadena;
	}

	public static void mensaje(String mensaje, int titulo) {

		String tituloSuperior = "";

		int tipo = 0;

		switch (titulo) {

		case 1:
			tipo = JOptionPane.ERROR_MESSAGE;
			tituloSuperior = "Error";

			break;

		case 2:
			tipo = JOptionPane.INFORMATION_MESSAGE;
			tituloSuperior = "Informacion";

			break;

		case 3:
			tipo = JOptionPane.WARNING_MESSAGE;
			tituloSuperior = "Advertencia";

			break;

		default:
			break;

		}

		JLabel alerta = new JLabel(mensaje);

		alerta.setFont(new Font("Arial", Font.BOLD, 18));

		JOptionPane.showMessageDialog(null, alerta, tituloSuperior, tipo);

	}

	public static void eliminarFichero(String archivo) {

		File fichero = new File(archivo);

		if (fichero.exists()) {
			fichero.delete();
		}

	}

	public static void abrirCarpeta(String ruta) throws IOException {

		if (ruta != null && !ruta.equals("") && !ruta.isEmpty()) {

			try {

				if (Main.getOs().contentEquals("Linux")) {
					Runtime.getRuntime().exec("xdg-open " + ruta);
				}

				else {
					Runtime.getRuntime().exec("cmd /c explorer " + "\"" + ruta + "\"");
				}
			} catch (IOException e) {
				mensaje("Ruta inválida", 1);
			}
		}

	}

	public static int listarFicherosPorCarpeta(final File carpeta) {

		int ocurrencias = 0;

		String extension = "";

		ArrayList<String> permitidos = new ArrayList<>();

		permitidos.add(".jpg");
		permitidos.add(".JPG");
		permitidos.add("jpeg");
		permitidos.add(".png");
		permitidos.add(".PNG");
		permitidos.add(".gif");
		permitidos.add(".GIF");

		for (final File ficheroEntrada : carpeta.listFiles()) {

			if (ficheroEntrada.getName().indexOf('.') > -1) {
				extension = ficheroEntrada.getName().substring(ficheroEntrada.getName().length() - 4,
						ficheroEntrada.getName().length());

				if (permitidos.contains(extension)) {
					ocurrencias++;
				}

			}
		}

		return ocurrencias;
	}

	public static void crearCarpetas() {

		File directorio = new File(Main.directorioActual + "Config");
		directorio.mkdir();

		directorio = new File(Main.directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar");
		directorio.mkdir();

		directorio = new File(Main.directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar"
				+ Main.getSeparador() + "recortes");
		directorio.mkdir();

		directorio = new File(Main.directorioActual + "Config" + Main.getSeparador() + "imagenes_para_recortar"
				+ Main.getSeparador() + "recortes" + Main.getSeparador() + "Image_rotate");
		directorio.mkdir();

	}

	public static String[] leerFicheroArray(String fichero, int longitud) {

		String[] salida = new String[longitud];

		String texto = "";

		int i = 0;

		FileReader flE = null;

		BufferedReader fE = null;

		try {

			flE = new FileReader(fichero);

			fE = new BufferedReader(flE);

			texto = fE.readLine();

			while (texto != null && i < longitud) {

				salida[i] = texto;

				i++;

				texto = fE.readLine();

			}

			fE.close();

			flE.close();

		}

		catch (Exception e) {
			//
		}

		finally {

			if (fE != null) {

				try {
					fE.close();
				}

				catch (IOException e) {
					//
				}

			}

			if (flE != null) {

				try {
					flE.close();
				}

				catch (IOException e) {
					//
				}

			}
		}

		return salida;

	}

	public static java.io.File[] seleccionar(int tipo, String rotulo, String mensaje) {

		JFileChooser chooser = new JFileChooser();

		FileNameExtensionFilter filter = null;

		switch (tipo) {

		case 1:
			filter = new FileNameExtensionFilter(rotulo, "jpg");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			break;

		case 2:
			filter = new FileNameExtensionFilter(rotulo, "jpg", "gif", "jpeg", "png", "avi", "mp4");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			break;

		case 3:
			filter = new FileNameExtensionFilter(rotulo, "txt");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			break;

		default:
			break;
		}

		if (!chooser.isMultiSelectionEnabled()) {
			chooser.setMultiSelectionEnabled(true);
		}

		chooser.showOpenDialog(chooser);

		File[] files = chooser.getSelectedFiles();

		if (files.length == 0) {
			mensaje(mensaje, 3);
		}

		return files;
	}

	public static int listarFicherosPorCarpeta(final File carpeta, String filtro) {

		int ocurrencias = 0;

		String extension;

		String nombreArchivo;

		for (final File ficheroEntrada : carpeta.listFiles()) {

			nombreArchivo = ficheroEntrada.getName();

			extension = extraerExtension(nombreArchivo);

			if (extension.equals(filtro) || filtro.equals(".")) {

				ocurrencias++;
			}

		}

		return ocurrencias;
	}

	public static void eliminarArchivos(String ruta) {

		LinkedList<String> frames = directorio(ruta, ".", true, false);

		for (int i = 0; i < frames.size(); i++) {

			if (!frames.get(i).isEmpty()) {

				eliminarFichero(ruta + frames.get(i));
			}

		}

	}

	public static void renombrarArchivos(List<String> list, String ruta) {

		File f1;

		File f2;

		for (int x = 0; x < list.size(); x++) {

			f1 = new File(ruta + list.get(x));

			f2 = new File(ruta + eliminarPuntos(list.get(x)));

			f1.renameTo(f2);

		}

	}

}
