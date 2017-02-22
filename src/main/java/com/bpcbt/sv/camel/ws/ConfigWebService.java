package com.bpcbt.sv.camel.ws;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import com.bpcbt.converters.remapper.Processor;
import com.bpcbt.sv.camel.Router;
import com.bpcbt.sv.camel.utils.FileUtils;
import com.bpcbt.sv.config.message.v1.Config;
import com.bpcbt.sv.config.message.v1.FileList;
import com.bpcbt.sv.config.message.v1.Remap;
import com.bpcbt.sv.config.message.v1.RemapResult;
import com.bpcbt.sv.config.message.v1.SaveResult;
import com.bpcbt.sv.config.message.v1.Void;
import com.bpcbt.sv.config.service.v1.ConfigPortType;
import com.bpcbt.sv.config.service.v1.Fault;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class ConfigWebService implements ConfigPortType {
	private static final Logger logger = LogManager.getLogger(ConfigWebService.class);

	@Autowired
	private Router router;
	@Value("${config_folder}")
	private String configFolder;

	@PostConstruct
	private void init() {
		configFolder = configFolder.replaceAll("(\\\\|/)", Matcher.quoteReplacement(File.separator));
	}

	@Override
	public Config getConfig(Config request) {
		String fileName = request.getFilename();
		logger.info("Received request for config file: " + fileName);
		String content;
		try {
			content = FileUtils.readFile(configFolder + fileName, request.getEncoding());
		} catch (Exception ex) {
			logger.error("File error", ex);
			content = ex.toString();
		}
		request.setConfig(content);
		return request;
	}

	@Override
	public FileList getFiles(Void request) {
		FileList response = new FileList();
		List<String> files = new ArrayList<>();
		getFiles(new File(configFolder), files);
		response.getFile().addAll(files);
		return response;
	}

	@Override
	public SaveResult saveConfig(Config request) {
		SaveResult result = new SaveResult();
		try {
			String content = ((String) request.getConfig()).replaceAll("(\\<\\!\\[CDATA\\[|\\]\\]\\>)", "").trim();
			if (!content.trim().isEmpty()) {
				FileUtils.writeFile(content, configFolder + request.getFilename(), request.getEncoding());
			} else {
				throw new Exception("Empty config content");
			}
			router.reloadConvertersConfigs(request.getFilename());
			result.setResult(true);
		} catch (Exception ex) {
			logger.error("File error", ex);
			result.setResult(false);
			result.setErrorMessage(ex.toString());
		}
		return result;
	}

	private void getFiles(File folder, List<String> list) {
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				list.add((file.getParent() + File.separator + file.getName()).replace(
						configFolder, ""));
			} else {
				getFiles(file, list);
			}
		}
	}

	@Override
	public RemapResult remapConfigs(Remap request) throws Fault {
		RemapResult result = new RemapResult();
		try {
			Processor processor = new Processor();
			processor.processAll(request.getConfDir().trim(), request.getConfDirExt().trim());
			result.setResult("Ok");
		} catch (FileNotFoundException e) {
			logger.error("Remap error", e);
			com.bpcbt.sv.config.message.v1.Fault fault = new com.bpcbt.sv.config.message.v1.Fault();
			fault.setCode("100");
			fault.setDescription(e.getMessage());
			throw new Fault(e.getMessage(), fault);
		} catch (JAXBException e) {
			logger.error("Remap error", e);
			com.bpcbt.sv.config.message.v1.Fault fault = new com.bpcbt.sv.config.message.v1.Fault();
			fault.setCode("101");
			fault.setDescription(e.getMessage());
			throw new Fault(e.getMessage(), fault);
		} catch (Exception e) {
			logger.error("Remap error", e);
			throw new Fault("ERROR", e);
		}
		return result;
	}
}
