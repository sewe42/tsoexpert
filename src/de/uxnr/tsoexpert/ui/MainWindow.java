package de.uxnr.tsoexpert.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import de.uxnr.tsoexpert.TSOExpert;
import de.uxnr.tsoexpert.proxy.GameHandler;
import de.uxnr.tsoexpert.proxy.StaticHandler;
import de.uxnr.tsoexpert.ui.tab.BuildingTab;
import de.uxnr.tsoexpert.ui.tab.DepositTab;
import de.uxnr.tsoexpert.ui.tab.FileCacheTab;
import de.uxnr.tsoexpert.ui.tab.ResourceTab;
import de.uxnr.tsoexpert.ui.tab.ZoneMapTab;

public class MainWindow {
	private JFrame frame;
	private JTabbedPane tabbedPane;

	private StatusBar statusBar;

	private ZoneMapTab zoneMapTab;
	private BuildingTab buildingTab;
	private ResourceTab resourceTab;
	private DepositTab depositTab;
	private FileCacheTab fileCacheTab;

	/**
	 * Launch the application.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		TSOExpert.launchProxy();

		final Thread proxy = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream stream = new FileInputStream(new File("2.amf"));
					GameHandler gameHandler = (GameHandler) TSOExpert.getHandler("GameHandler");
					gameHandler.parseAMF(stream);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		TSOExpert.launchWindow();
		proxy.start();
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public MainWindow() {
		GameHandler gameHandler = (GameHandler) TSOExpert.getHandler("GameHandler");
		StaticHandler staticHandler = (StaticHandler) TSOExpert.getHandler("StaticHandler");

		this.initialize();

		this.zoneMapTab.bind(gameHandler);
		this.buildingTab.bind(gameHandler);
		this.resourceTab.bind(gameHandler);
		this.depositTab.bind(gameHandler);
		this.fileCacheTab.bind(staticHandler);
	}

	public void show() {
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.frame = new JFrame("TSO Expert");
		this.frame.setBounds(100, 100, 700, 450);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.statusBar = new StatusBar(this.frame);
		this.frame.add(this.statusBar, BorderLayout.SOUTH);

		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.frame.getContentPane().add(this.tabbedPane, BorderLayout.CENTER);

		this.zoneMapTab = new ZoneMapTab();
		this.tabbedPane.addTab("Map", null, this.zoneMapTab, null);

		this.buildingTab = new BuildingTab();
		this.tabbedPane.addTab("Buildings", null, this.buildingTab, null);

		this.resourceTab = new ResourceTab();
		this.tabbedPane.addTab("Resources", null, this.resourceTab, null);

		this.depositTab = new DepositTab();
		this.tabbedPane.addTab("Deposits", null, this.depositTab, null);

		this.fileCacheTab = new FileCacheTab();
		this.tabbedPane.addTab("Files", null, this.fileCacheTab, null);
	}
}
