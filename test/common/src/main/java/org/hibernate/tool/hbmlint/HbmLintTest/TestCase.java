package org.hibernate.tool.hbmlint.HbmLintTest;

import java.io.File;

import org.hibernate.tool.hbm2x.HbmLintExporter;
import org.hibernate.tool.hbmlint.Detector;
import org.hibernate.tool.hbmlint.HbmLint;
import org.hibernate.tool.hbmlint.detector.BadCachingDetector;
import org.hibernate.tool.hbmlint.detector.InstrumentationDetector;
import org.hibernate.tool.hbmlint.detector.ShadowedIdentifierDetector;
import org.hibernate.tool.metadata.MetadataSources;
import org.hibernate.tools.test.util.HibernateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCase {

	private static final String[] HBM_XML_FILES = new String[] {
			"CachingSettings.hbm.xml",
			"IdentifierIssues.hbm.xml",
			"BrokenLazy.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File outputDir = null;
	private File resourcesDir = null;
	
	private MetadataSources metadataSources = null;
	
	@Before
	public void setUp() {
		outputDir = new File(temporaryFolder.getRoot(), "output");
		outputDir.mkdir();
		resourcesDir = new File(temporaryFolder.getRoot(), "resources");
		resourcesDir.mkdir();
		metadataSources = HibernateUtil.initializeMetadataSources(this, HBM_XML_FILES, resourcesDir);
	}
	
	@Test
	public void testExporter() {	
		HbmLintExporter exporter = new HbmLintExporter();		
		exporter.setMetadata(metadataSources.buildMetadata());
		exporter.setOutputDirectory(outputDir);
		exporter.start();
	}
	
	@Test
	public void testValidateCache() {	
		HbmLint analyzer = new HbmLint(new Detector[] { new BadCachingDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		Assert.assertEquals(1,analyzer.getResults().size());		
	}

	@Test
	public void testValidateIdentifier() {		
		HbmLint analyzer = new HbmLint(new Detector[] { new ShadowedIdentifierDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		Assert.assertEquals(1,analyzer.getResults().size());
	}
	
	@Test
	public void testBytecodeRestrictions() {		
		HbmLint analyzer = new HbmLint(new Detector[] { new InstrumentationDetector() });		
		analyzer.analyze(metadataSources.buildMetadata());
		Assert.assertEquals(analyzer.getResults().toString(), 2,analyzer.getResults().size());
	}
	
}
