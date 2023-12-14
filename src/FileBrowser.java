package ce326.hw3;

import javax.swing.BoxLayout;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;  
import org.w3c.dom.Node;   
import org.xml.sax.SAXException;
import javax.swing.JList;

public class FileBrowser extends JFrame{
  
  static final long serialVersionUID = -4567891456L;  
  JPanel mainPanel;
  JLabel Label;
  JScrollPane scrollPane;
  JPanel Search;
  JScrollPane dirFiles;
  JPanel path;
  Pair<JButton, File> pair;
  List<Pair<JButton, File>> pairList;
  List<Pair<JButton, File>> currComp;
  List<Pair<JButton, File>> currFav;
  File currFile;
  File currDir;
  JMenu editMenu;
  JPanel inBetween;
  File currRightFile;
  JPopupMenu Popup;
  JMenuItem Paste;
  JMenuItem PopPaste;
  JMenuItem GeneralPaste;
  int rightClickFlag;
  int leftClickFlag;
  JCheckBoxMenuItem Hidden;
  JPopupMenu GeneralPopup;
  File CutCopyFile;
  File CutCopyDir;          
  int CutCopyFlag;
  public static final File favFile= new File(".java-file-browser/properties.xml");
  JScrollPane Favourites;
  JPanel inInBetween;
  String favName;
  JTextField searchBar;
  List<String> SearchResults;
  JList<String> SearchList;
  JMenuItem AddtoFavourites;
  
  public static final int MINIMUM_WIDTH = 500;
  public static final int MINIMUM_HEIGHT = 400;

  //Dhmiourgei to JFrame kai kalei thn addMenu gia thn arxikopoihsh tou menu.
  public FileBrowser() {
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setPreferredSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
    mainPanel = new JPanel(new BorderLayout());
    
    addMenu();
    pack();
    setVisible(true);
  }  
  
  //Dhmiourgei to kentriko orizontio menu kai kalei thn createMenu, opou dhmiourgountai ta upoloipa components tou menu.
  public void addMenu() {        
    String currentDirectory = System.getProperty("user.dir");
    File dir = new File(currentDirectory); 
    
    JMenuBar menuBar = new JMenuBar();
    
    //Dhmiourgia tou file menu.
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(fileMenu);
    
    JMenuItem Exit = new JMenuItem("Exit");
    Exit.setMnemonic(KeyEvent.VK_E);
    
    fileMenu.add(Exit);
    
    Exit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
           System.exit(0);
      }
    });
    
    //Dhmiourgia tou edit menu.
    editMenu = new JMenu("Edit");
    editMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(editMenu);
    
    JMenuItem Cut= new JMenuItem("Cut");
    Cut.setMnemonic(KeyEvent.VK_C);
    
    editMenu.add(Cut);
    
    Cut.addActionListener(new CutListener());
    
    JMenuItem Copy= new JMenuItem("Copy");
    Copy.setMnemonic(KeyEvent.VK_E);
    
    editMenu.add(Copy);
    
    Copy.addActionListener(new CopyListener());
    
    Paste= new JMenuItem("Paste");
    Paste.setMnemonic(KeyEvent.VK_P);
    
    editMenu.add(Paste);
    Paste.addActionListener(new PasteListener());
    
    JMenuItem Rename= new JMenuItem("Rename");
    Rename.setMnemonic(KeyEvent.VK_P);
    
    editMenu.add(Rename);
    Rename.addActionListener(new RenameListener());
    
    JMenuItem Delete= new JMenuItem("Delete");
    Delete.setMnemonic(KeyEvent.VK_P);
    
    editMenu.add(Delete);
    Delete.addActionListener(new DeleteListener());
    
    AddtoFavourites= new JMenuItem("Add to Favourites");
    AddtoFavourites.setMnemonic(KeyEvent.VK_P);
    
    editMenu.add(AddtoFavourites);
    AddtoFavourites.addActionListener(new addToFavouritesListener());
    
    JMenuItem Properties= new JMenuItem("Properties");
    Properties.setMnemonic(KeyEvent.VK_P);
    
    editMenu.add(Properties);
    Properties.addActionListener(new PropertiesListener());
    
    //Dhmiourgia tou view menu.
    JMenu viewMenu = new JMenu("View");
    viewMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(viewMenu);
    
    JCheckBoxMenuItem Searching= new JCheckBoxMenuItem("Search");
    Searching.setMnemonic(KeyEvent.VK_C);
    
    viewMenu.add(Searching);
    
    Searching.addItemListener(new SearchListener());
    
    Hidden= new JCheckBoxMenuItem("Hidden Files/Folders");
    Hidden.setMnemonic(KeyEvent.VK_C);
    
    viewMenu.add(Hidden);
    Hidden.addItemListener(new HiddenListener());
    
    createMenu(dir);
    Search.setVisible(false);
    
    setJMenuBar(menuBar);
  }
  
    //Dhmiourgei pop up menu gia ton trexwn katalogo se xwro opou den uparxoun eikonidia.
    public void createGeneralPopUpMenu(){

        GeneralPopup = new JPopupMenu();
        GeneralPaste= new JMenuItem("Paste");
        GeneralPaste.setMnemonic(KeyEvent.VK_P);

        GeneralPopup.add(GeneralPaste);
        GeneralPaste.addActionListener(new PasteListener());
        if(CutCopyDir!=null){
            boolean areSame= (CutCopyDir.toString().equals(currDir.toString()));
            if((areSame) || (CutCopyFlag==2)){
               GeneralPaste.setEnabled(false);
            }
            else{
               GeneralPaste.setEnabled(true); 
            }
        }
        else{
            GeneralPaste.setEnabled(false);
        }
    }
    
    //Dhmiourgei pop up menu gia ta eikonidia tou trexwn katalogou.
    public void createPopUpMenu(){
        Popup= new JPopupMenu();

        JMenuItem  PopCut= new JMenuItem("Cut");
        PopCut.setMnemonic(KeyEvent.VK_C);

        Popup.add(PopCut);
        PopCut.addActionListener(new CutListener());

        JMenuItem PopCopy= new JMenuItem("Copy");
        PopCopy.setMnemonic(KeyEvent.VK_E);

        Popup.add(PopCopy);
        PopCopy.addActionListener(new CopyListener());

        PopPaste= new JMenuItem("Paste");
        PopPaste.setMnemonic(KeyEvent.VK_P);

        Popup.add(PopPaste);
        PopPaste.addActionListener(new PasteListener2());
        
        if(CutCopyDir!=null){
            boolean areSame= (CutCopyDir.toString().equals(currRightFile.toString()));
            boolean dir= currRightFile.isDirectory();
            if((areSame) || (CutCopyFlag==2) || (!dir)){
               PopPaste.setEnabled(false);
            }
            else{
               PopPaste.setEnabled(true); 
            }
        }
        else{
            PopPaste.setEnabled(false);
        }

        JMenuItem Rename= new JMenuItem("Rename");
        Rename.setMnemonic(KeyEvent.VK_P);

        Popup.add(Rename);
        Rename.addActionListener(new RenameListener());

        JMenuItem Delete= new JMenuItem("Delete");
        Delete.setMnemonic(KeyEvent.VK_P);

        Popup.add(Delete);
        Delete.addActionListener(new DeleteListener());

        JMenuItem AddtoFavourites= new JMenuItem("Add to Favourites");
        AddtoFavourites.setMnemonic(KeyEvent.VK_P);
        if(!currRightFile.isDirectory()){
                 AddtoFavourites.setEnabled(false);
              }
              else{
                 AddtoFavourites.setEnabled(true); 
        }

        Popup.add(AddtoFavourites);
        AddtoFavourites.addActionListener(new addToFavouritesListener());

        JMenuItem Properties= new JMenuItem("Properties");
        Properties.setMnemonic(KeyEvent.VK_P);

        Popup.add(Properties);
        Properties.addActionListener(new PropertiesListener());
        
    }
    
    //Main sunarthsh pou kalei ton kataskeuasth FileBrowser.
    public static void main(String[] args) {
        FileBrowser menu = new FileBrowser();
    }
    
    //Dhmiourgia tou main panel.
    public void createMenu(java.io.File dir){
        
        CutCopyFlag=2;
        currDir=dir;
        editMenu.setEnabled(false);
        Paste.setEnabled(false);
        currComp=null;
        inBetween= new JPanel();
        inInBetween= new JPanel();

        inBetween.setLayout(new BorderLayout());
        dirFiles=directory(dir);
        dirFiles.addMouseListener(new DirectoryAdapter());
        
        inBetween.add(dirFiles,BorderLayout.CENTER);


        path=breadcrump(dir);
        inBetween.add(path,BorderLayout.NORTH);

        Search = new JPanel();
        searchBar = new JTextField(10);
        Search.add(searchBar);
        JButton searchButton=new JButton("Search");
        Search.add(searchButton);
        searchButton.addActionListener(new SearchButtonListener());

        inInBetween.setLayout(new BorderLayout());
        inInBetween.add(inBetween, BorderLayout.CENTER);
        inInBetween.add(Search, BorderLayout.NORTH);
        
        try {
            createFavourites(dir);
        } catch (IOException ex) {
            Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }

        Favourites = favourites(favFile);

        mainPanel.add(inInBetween, BorderLayout.CENTER);
        mainPanel.add(Favourites,BorderLayout.WEST);

        
        setContentPane(mainPanel);
        revalidate();
        HideHidden();
        
    }
    
    //Kanei update to directory kai to breadcrumb.
    public void updateMenu(java.io.File dir){
          
        currDir=dir;
        editMenu.setEnabled(false);
        inBetween.removeAll();
        revalidate();
        inBetween.repaint();
        
        dirFiles.removeAll();
        dirFiles.repaint();
        dirFiles=directory(dir);
        dirFiles.addMouseListener(new DirectoryAdapter());
        
        inBetween.add(dirFiles,BorderLayout.CENTER);
        
        path.removeAll();
        path.repaint();
        path=breadcrump(dir);
        inBetween.add(path,BorderLayout.NORTH);
        
        if(Hidden.isSelected()){
            ShowHidden();
        }
        else{
            HideHidden();
        }

        if(CutCopyDir!=null){
            boolean areSame= (CutCopyDir.toString().equals(currDir.toString()));
            if((areSame) || (CutCopyFlag==2)){
               Paste.setEnabled(false);
            }
            else{
               Paste.setEnabled(true); 
            }
        }
        else{
            Paste.setEnabled(false);
        }
        revalidate();
    }
    
    //Diagrafei to agaphmeno pou epilexthike.
    public void deleteFav(String name){
        
         try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse(favFile);
            doc.getDocumentElement().normalize();  

            NodeList nodeList = doc.getElementsByTagName("directory"); 

            for (int i = 0; i < nodeList.getLength(); i++) {
              Node node = nodeList.item(i); 
              if (node.getNodeType() == Node.ELEMENT_NODE) {

                  Element elem = (Element) node;
                  String naming = elem.getAttribute("name");
                  if(naming.equals(name)){
                      node.getParentNode().removeChild(node);
                  }
              }
            }
            
            
            DOMSource source = new DOMSource(doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(favFile);
            transformer.transform(source, result);
          }catch (Exception e){  
        e.printStackTrace();  
      }
        
    }
    
    //Dhmiourgei to JPanel twn agaphmenwn, basizomeno sto xml arxeio pou uparxei.
    public JScrollPane favourites(java.io.File properties){
        
      Pair<JButton,File> createPair;
      JPanel favPanel= new JPanel();
      JScrollPane favScrollPanel;
      currFav= new ArrayList<>();
      int initialFlag=0;
      
      try {
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();  
          Document doc = db.parse(properties);
          doc.getDocumentElement().normalize();  
          
          NodeList nodeList = doc.getElementsByTagName("directory"); 
          
          for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i); 
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                
                Element elem = (Element) node;
                String name = elem.getAttribute("name");
                String pathTo = elem.getAttribute("path");
                ImageIcon icon = new ImageIcon("./icons/folder.png");
                JButton input = new JButton(icon);
                input.setText(name);
                input.setHorizontalTextPosition(JLabel.CENTER);
                input.setVerticalTextPosition(JLabel.BOTTOM);
                input.setSize(new Dimension(100,100));
                input.setPreferredSize(new Dimension(100,100));
                input.setMinimumSize(new Dimension(100,100));
                input.setMaximumSize(new Dimension(100,100));
                input.setToolTipText(name);
                input.addMouseListener(new FavouritesAdapter());
                File PathTo=new File(pathTo+File.separatorChar);
                
                createPair=Pair.createPair(input, PathTo);
                currFav.add(createPair);
                favPanel.add(input);
            }
          }
          
          favPanel.setLayout(new BoxLayout(favPanel, BoxLayout.Y_AXIS));
          favScrollPanel = new JScrollPane(favPanel);
          favScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
          favScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          favScrollPanel.setPreferredSize(new Dimension(120,500));

          return favScrollPanel;
      }catch (Exception e){  
        e.printStackTrace();  
      }
      
      return(null);
    }
    
    //Prosthetei ena arxeio sto xml arxeio twn agaphmenwn.
    public void editfavFile(java.io.File dir){
      String name;  
      int i;
      
      try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(favFile);
            Element root = document.getDocumentElement();
            
            Element directory = document.createElement("directory");

            root.appendChild(directory);

            for(i=dir.toString().length()-1;i>=0;i--){
                if(dir.toString().charAt(i)==File.separatorChar){
                   break; 
                }
            }

            name= dir.toString().substring(i+1,dir.toString().length());

            Attr attr = document.createAttribute("name");
            attr.setValue(name);
            directory.setAttributeNode(attr);

            Attr attr2 = document.createAttribute("path");
            attr2.setValue(dir.toString());
            directory.setAttributeNode(attr2);

            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(favFile);
            transformer.transform(source, result);
          } catch (SAXException ex) {
              Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
          } catch (IOException ex) {
              Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
          } catch (TransformerException ex) {
                  Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
          } catch (ParserConfigurationException ex) {
          Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
      }

        
    }
    
     //Prosthetei ena arxeio sta agaphmena.
     public void addToFavourites(java.io.File dir){ 
        BorderLayout layout = (BorderLayout)mainPanel.getLayout();
        mainPanel.remove(layout.getLayoutComponent(BorderLayout.WEST));
        Favourites.removeAll();
        Favourites.repaint();
        if(rightClickFlag==1){
            editfavFile(currRightFile);
        }
        else if(leftClickFlag==1){
            editfavFile(currFile);   
        }
        Favourites=favourites(favFile);
        mainPanel.add(Favourites,BorderLayout.WEST);
        revalidate();
     }
             
    /*Dhmiourgei ton fakelo ".java-file-browser" an den uparxei kai mesa ston fakelo dhmiourgei,an den uparxei, to arxeio
     "properties.xml",opou apothikeuontai ta agaphmena. Ean to arxeio den to periexei, topothetei to home directory tou xrhsth.*/
    public void createFavourites(java.io.File dir) throws IOException{
        File favDir= new File(".java-file-browser/");
        boolean exists = favDir.exists();
        String name;
        int i;
        
        if(!exists){
            if(favDir.mkdir()){
                
                favFile.createNewFile();
                try {
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

                Document document = documentBuilder.newDocument();

                Element root = document.createElement("favourites");
                document.appendChild(root);

                Element directory = document.createElement("directory");

                root.appendChild(directory);
                
                for(i=dir.toString().length()-1;i>=0;i--){
                    if(dir.toString().charAt(i)==File.separatorChar){
                       break; 
                    }
                }
                
                name= dir.toString().substring(i+1,dir.toString().length());
                
                Attr attr = document.createAttribute("name");
                attr.setValue(name);
                directory.setAttributeNode(attr);

                Attr attr2 = document.createAttribute("path");
                attr2.setValue(dir.toString());
                directory.setAttributeNode(attr2);
                
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(favFile);
 
                transformer.transform(domSource, streamResult);
 
                }catch(ParserConfigurationException pce) {
                    pce.printStackTrace();
                } catch (TransformerException tfe) {
                    tfe.printStackTrace();
                }
               
            }
        }
        
       
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(favFile);
            Element root = document.getDocumentElement();
            int initialFlag=0;
            
            NodeList nodeList = document.getElementsByTagName("directory"); 
            String currentDirectory = System.getProperty("user.dir");
            File initial = new File(currentDirectory); 
          
            for (i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i); 
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                  Element elem = (Element) node;
                  String pathTo = elem.getAttribute("path");
                  if(pathTo.equals(initial.toString())){
                     initialFlag=1; 
                  }
              }
            }
            if(initialFlag!=1){
                Element directory = document.createElement("directory");

                root.appendChild(directory);

                for(i=initial.toString().length()-1;i>=0;i--){
                    if(initial.toString().charAt(i)==File.separatorChar){
                       break; 
                    }
                }

                name= initial.toString().substring(i+1,initial.toString().length());

                Attr attr = document.createAttribute("name");
                attr.setValue(name);
                directory.setAttributeNode(attr);

                Attr attr2 = document.createAttribute("path");
                attr2.setValue(initial.toString());
                directory.setAttributeNode(attr2);
            }

            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(favFile);
            transformer.transform(source, result);
          } catch (SAXException ex) {
              Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
          } catch (IOException ex) {
              Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
          } catch (TransformerException ex) {
                  Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
          } catch (ParserConfigurationException ex) {
          Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
 
    //Dhmiourgei to breadcrump.
    public JPanel breadcrump(java.io.File dir){
        
        JPanel Breadcrumb = new JPanel();
        int NumOfBackslash=0;
        String dirtoString= dir.toString();
        String var="";
        Pair<JButton,File> createPair;
        pairList=new ArrayList<>();
        File BreadPath;
        pairList.clear();
        
        
        for(int i=0;i<dirtoString.length();i++){
           if(dirtoString.charAt(i)==File.separatorChar){
               NumOfBackslash++;
           }
        }
       
        if((NumOfBackslash==1)&(dirtoString.indexOf(File.separatorChar)==(dirtoString.length()-1))){
           var=var+dirtoString+File.separatorChar;
           BreadPath=new File(var);
           JButton input=new JButton(dirtoString.substring(0,(dirtoString.length()-1)));
           createPair=Pair.createPair(input, BreadPath);
           pairList.add(createPair);
           input.addActionListener(new BreadcrumpListener());
           Breadcrumb.add(input); 
           return(Breadcrumb);
        }
        while(NumOfBackslash!=0){
            for(int i=0;i<dirtoString.length();i++){
                if(dirtoString.charAt(i)==File.separatorChar){
                    var=var+dirtoString.substring(0,i)+File.separatorChar;
                    BreadPath=new File(var);
                    JButton input=new JButton(dirtoString.substring(0,i));
                    createPair=Pair.createPair(input, BreadPath);
                    pairList.add(createPair);
                    input.addActionListener(new BreadcrumpListener());
                    Breadcrumb.add(input);
                    dirtoString=dirtoString.substring(i+1,(dirtoString.length()));
                    Breadcrumb.add(new JLabel(">"));
                    NumOfBackslash--;
                    break;
                }
            }
        }
        
        
        Breadcrumb.add(new JButton(dirtoString.substring(0,(dirtoString.length()))));
        Breadcrumb.setLayout(new WrapLayout());
        
        return Breadcrumb;
    }
    
    //Dhmiourgia panel gia ta periexomena tou trexwn katalogou.
    public JScrollPane directory(java.io.File dir){    
        JScrollPane DirectoryContents;
        File[] listOfFiles = dir.listFiles();
        currComp= new ArrayList<>();
        currComp.clear();

        int dirNum = 0, fileNum = 0;
        JButton input;
        JPanel panel = new JPanel();
               
        for (File file : listOfFiles) {
            boolean isdirectory = file.isDirectory();
            if(isdirectory){
               dirNum++; 
            }
            else{
               fileNum++; 
            }
        }
        
        File[] listOfDir=new File[dirNum];
        File[] listOfActualFiles=new File[fileNum];
        int i=0;
        int j=0;
        for (File file : listOfFiles) {
           boolean isdirectory = file.isDirectory();
           if(isdirectory){
               listOfDir[i]=file;
               i++;
            }
            else{
               listOfActualFiles[j]=file;
               j++;
            }
        }
        
        Arrays.sort(listOfDir);
        Arrays.sort(listOfActualFiles);
        
        System.arraycopy(listOfDir, 0, listOfFiles, 0, dirNum);  
        System.arraycopy(listOfActualFiles, 0, listOfFiles, dirNum, fileNum);  
        
        for (File file : listOfFiles) {
            boolean exists = file.exists(); 
            if(exists){
                input= CreateButton(file);
                panel.add(input);           
            }
        }
        
        panel.setLayout(new WrapLayout());
        DirectoryContents = new JScrollPane(panel);
        DirectoryContents.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        DirectoryContents.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        DirectoryContents.setPreferredSize(new Dimension(300,300));
        
       return DirectoryContents;
    }   
    
    //Dhmiourgia JButton gia kathe arxeio/fakelo tou katalogou.
    public JButton CreateButton(java.io.File file){
       String fileName = file.toString();
       JButton button;
       String fullName;
       int i;
       String extension="";
       
       fullName = file.getParent();
       fullName = file.toString().replace(fullName, "");
       if(fullName.charAt(0)==File.separatorChar){
          fullName = fullName.substring(1,fullName.length());
       }
       
       boolean dir= file.isDirectory();
       if((!dir)&&(fullName.charAt(0)!='.')){
        int indexing = fullName.lastIndexOf('.');
        if(indexing!=-1){
         fullName= fullName.substring(0,indexing);
        }
       }
       
       boolean isdirectory = file.isDirectory();
       if(isdirectory){
          ImageIcon icon = new ImageIcon("./icons/folder.png");
          button = new JButton(icon);
          button.setText(fullName);
          button.setHorizontalTextPosition(JLabel.CENTER);
          button.setVerticalTextPosition(JLabel.BOTTOM);
          button.setSize(new Dimension(100,100));
          button.setPreferredSize(new Dimension(100,100));
          button.setMinimumSize(new Dimension(100,100));
          button.setToolTipText(fullName);
          currComp.add(Pair.createPair(button, file));
          button.addMouseListener(new MyMouseAdapter()); 
          return(button);  
       }
       else{
           if(fullName.charAt(0)!='.'){
                int index = fileName.lastIndexOf('.');
                if(index > 0) {
                     extension = fileName.substring(index + 1);
                }
           }
           else{
              extension= "unknown"; 
           }
            switch(extension){
                case "audio":
                case "mp3":
                case "ogg":
                case "wav":
                    ImageIcon iconA = new ImageIcon("./icons/audio.png");
                    button = new JButton(iconA);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM);
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button); 
                case "bmp":
                case "giff":
                case "png":
                case "image":
                case "jpeg":
                case "jpg":
                    ImageIcon iconB = new ImageIcon("./icons/bmp.png");
                    button = new JButton(iconB);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM); 
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                case "doc":
                case "docx":
                case "odt":    
                    ImageIcon iconC = new ImageIcon("./icons/doc.png");
                    button = new JButton(iconC);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM);
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                case "xlsx":
                case "xlx":
                case "ods":    
                    ImageIcon iconD = new ImageIcon("./icons/xlsx.png");
                    button = new JButton(iconD);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM); 
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                case "gz":
                case "tar":
                case "tgz":  
                case "zip": 
                    ImageIcon iconE = new ImageIcon("./icons/gz.png");
                    button = new JButton(iconE);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM);
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                case "htm":
                case "html":
                case "xml":  
                    ImageIcon iconF = new ImageIcon("./icons/htm.png");
                    button= new JButton(iconF);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM);
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                case "pdf":  
                    ImageIcon iconG = new ImageIcon("./icons/pdf.png");
                    button = new JButton(iconG);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM); 
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                case "txt":  
                    ImageIcon iconJ = new ImageIcon("./icons/txt.png");
                    button = new JButton(iconJ);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM);
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                case "video":  
                    ImageIcon iconI = new ImageIcon("./icons/video.png");
                    button = new JButton(iconI);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM); 
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
                default:
                    ImageIcon iconK = new ImageIcon("./icons/question.png");
                    button = new JButton(iconK);
                    button.setText(fullName);
                    button.setHorizontalTextPosition(JLabel.CENTER);
                    button.setVerticalTextPosition(JLabel.BOTTOM);
                    button.setSize(new Dimension(100,100));
                    button.setPreferredSize(new Dimension(100,100));
                    button.setMinimumSize(new Dimension(100,100));
                    button.setToolTipText(fullName);
                    currComp.add(Pair.createPair(button, file));
                    button.addMouseListener(new MyMouseAdapter()); 
                    return(button);
            }
            
       }
       
    }
    
    //Ypologizei to megethos enos arxeiou.
    long calculateSize(java.io.File dir){
        long fullsize=0;
        boolean isdirectory= dir.isDirectory();
        if(!isdirectory){
            return dir.length();
        }
        else{
            File[] listOfFiles = dir.listFiles();
            for (File file : listOfFiles) {
                boolean isDirectory= file.isDirectory();
                if(isDirectory){
                  fullsize= fullsize+calculateSize(file);  
                }
                else{
                  fullsize= fullsize+file.length();
                }
            }
            return fullsize;
        }
    }
    
    //Diagrafei anadromika ena fakelo.
    public void recursiveDeletion(java.io.File dir){
        
        File[] listOfFiles = dir.listFiles();
        int failedDeletion=0;
               
        for (File file : listOfFiles) {
            boolean isdirectory = file.isDirectory();
            if(isdirectory){
               recursiveDeletion(file); 
            }
            else{
               if(file.delete()){}
               else{
                    JOptionPane.showMessageDialog(dirFiles,file.toString()+" deletion failed!","ERROR",JOptionPane.ERROR_MESSAGE);
                    failedDeletion=1;
               } 
            }
        }
        
        if(failedDeletion!=1){
           if(dir.delete()){}
               else{
                    JOptionPane.showMessageDialog(dirFiles,dir.toString()+" deletion failed!","ERROR",JOptionPane.ERROR_MESSAGE);
           } 
        }
        
    }
    
    //Antigrafei ena arxeio/fakelo.
    public static void copyDirectory(File sourceDir, File targetDir)
    throws IOException {
        if (sourceDir.isDirectory()) {
            copyDirectoryRecursively(sourceDir, targetDir);
        } else {
            Files.copy(sourceDir.toPath(), targetDir.toPath());
        }
    }
    
    //Anadromikh methodos gia thn antigrafh enos directory kai twn sub-diretories tou.
    private static void copyDirectoryRecursively(File source, File target)throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String child : source.list()) {
            copyDirectory(new File(source, child), new File(target, child));
        }
    }
    
    //Dhmiourgei mouse adapter gia ta eikonidia twn agaphmenwn.
    class FavouritesAdapter extends MouseAdapter{
        public void mouseClicked(MouseEvent e){
          JButton button = (JButton) e.getSource();
          int n;
          //Deksi klik.
          if(e.getButton()==MouseEvent.BUTTON3){
            JPopupMenu PopupMenu = new JPopupMenu();
            JMenuItem FavDelete= new JMenuItem("Delete");
            FavDelete.setMnemonic(KeyEvent.VK_P);

            PopupMenu.add(FavDelete);
            FavDelete.addActionListener(new DeleteFavouriteListener());
              
            PopupMenu.show(e.getComponent(), e.getX(), e.getY());
            favName = button.getText();
          }
          //Aristero klik.
          if(e.getButton()==MouseEvent.BUTTON1){
              int sizeoflist = currFav.size();
              for(n=0;n<sizeoflist;n++){
                if(currFav.get(n).getElement0()==button){
                    break; 
                }
              }
              
              updateMenu(currFav.get(n).getElement1());
          }
        }
    }
    
    //Dhmiourgei mouse adapter gia ta eikonidia tou trexwn katalogou.
    class MyMouseAdapter extends MouseAdapter{
        public void mouseClicked(MouseEvent e){
          JButton button = (JButton) e.getSource();
          int n;
          //Deksi klik.
          if(e.getButton()==MouseEvent.BUTTON3){
              rightClickFlag=1;
              leftClickFlag=0;
              editMenu.setEnabled(true);
              int sizeoflist = currComp.size();
              for(n=0;n<sizeoflist;n++){
                if(currComp.get(n).getElement0()==button){
                    break; 
                }
              }
              
              currRightFile=currComp.get(n).getElement1();
              currFile= currComp.get(n).getElement1();
              createPopUpMenu();
              Popup.show(e.getComponent(), e.getX(), e.getY());
          }
          //Aristero klik.
          if(e.getButton()==MouseEvent.BUTTON1){
              
              editMenu.setEnabled(true);
              int sizeoflist = currComp.size();
              for(n=0;n<sizeoflist;n++){
                if(currComp.get(n).getElement0()==button){
                    break; 
                }
              }
              
              if(!currComp.get(n).getElement1().isDirectory()){
                 AddtoFavourites.setEnabled(false);
              }
              else{
                 AddtoFavourites.setEnabled(true); 
              }
              
              if(currFile==null){
              currFile=currComp.get(n).getElement1();
              }
              else if(currFile==currComp.get(n).getElement1()&& (leftClickFlag==1)){
                boolean directoryFile = currComp.get(n).getElement1().isDirectory();
                boolean isreadable = Files.isReadable(currComp.get(n).getElement1().toPath());
                if(!isreadable){
                    JOptionPane.showMessageDialog(
                   dirFiles,
                   "Access denied!",
                   "ERROR",
                   JOptionPane.ERROR_MESSAGE);
                }
                else if(directoryFile){
                    updateMenu(currComp.get(n).getElement1());
                }
                else{
                    String executablePath = currComp.get(n).getElement1().toString();
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(currComp.get(n).getElement1());
                    } catch (IOException ex) {
                        Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
                    }      
                }
              }
              else{
                currFile=currComp.get(n).getElement1();
              }
              
              leftClickFlag=1;
              rightClickFlag=0;
          }
        }
    }
  
    //Dhmiourgei pop up menu gia deksi klik se xwro opou den uparxoun eikonidia.
    class DirectoryAdapter extends MouseAdapter{
      public void mouseClicked(MouseEvent e){
          //Deksi klik.
          if(e.getButton()==MouseEvent.BUTTON3){
              createGeneralPopUpMenu();
              GeneralPopup.show(e.getComponent(), e.getX(), e.getY());
          }
      }
    }
    
    //Dhmiourgei action listener gia to delete sta agaphmena.
    class DeleteFavouriteListener implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
            deleteFav(favName);
            BorderLayout layout = (BorderLayout)mainPanel.getLayout();
            mainPanel.remove(layout.getLayoutComponent(BorderLayout.WEST));
            Favourites.removeAll();
            Favourites.repaint();
            Favourites=favourites(favFile);
            mainPanel.add(Favourites,BorderLayout.WEST);
            revalidate();
        }
    }
    
    //Dhmiourgia action listener gia to breadcrumb.
    class BreadcrumpListener implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
           JButton button = (JButton) e.getSource();
           int n;
           
           int sizeoflist = pairList.size();
           for(n=0;n<sizeoflist;n++){
               if(pairList.get(n).getElement0()==button){
                  break; 
               }
           }
           updateMenu(pairList.get(n).getElement1());
           
        }    
    }
    
    //Dhmiourgia item listener gia to Search.
    class SearchListener implements ItemListener{
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Search.setVisible(true);
            } else {
                Search.setVisible(false);
            }
        }
    }
    
    //Dhmiourgia action listener gia to Cut.
    class CutListener implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
            CutCopyFlag=0;
            if(rightClickFlag==1){
                CutCopyFile= currRightFile;
                CutCopyDir= currDir;
            }
            else if(leftClickFlag==1){
                CutCopyFile= currFile;
                CutCopyDir= currDir;
            }
        }    
    }
    //Dhmiourgia action listener gia to Copy.
    class CopyListener implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
            CutCopyFlag=1;
            if(rightClickFlag==1){
                CutCopyFile= currRightFile;
                CutCopyDir= currDir;
            }
            else if(leftClickFlag==1){
                CutCopyFile= currFile;
                CutCopyDir= currDir;
            }
        }    
    }
    
    //Dhmiourgia action listener gia to Paste gia deksi klik se eikonidio.
    class PasteListener2 implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
            if(CutCopyFlag==0){
                String fullName = CutCopyFile.getParent();
                fullName = CutCopyFile.toString().replace(fullName, "");
                if(fullName.charAt(0)==File.separatorChar){
                   fullName = fullName.substring(1,fullName.length());
                } 
                File newer= new File(currRightFile.toString()+File.separatorChar+fullName);
                CutCopyFile.renameTo(newer);
                updateMenu(currDir);
            }
            else if(CutCopyFlag==1){
                boolean isdirectory= CutCopyFile.isDirectory();
                String fullName = CutCopyFile.getParent();
                fullName = CutCopyFile.toString().replace(fullName, "");
                if(fullName.charAt(0)==File.separatorChar){
                   fullName = fullName.substring(1,fullName.length());
                } 
                File newer= new File(currRightFile.toString()+File.separatorChar+fullName);
                if(!isdirectory){
                    try { 
                    FileReader fin = new FileReader(CutCopyFile);  
                    FileWriter fout = new FileWriter(newer, true);  
                    int c;  
                    while ((c = fin.read()) != -1) {  
                     fout.write(c);  
                    } 

                    fin.close();  
                    fout.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    updateMenu(currDir);
                }
                else{
                    try {
                        copyDirectoryRecursively(CutCopyFile,newer);
                    } catch (IOException ex) {
                        Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    updateMenu(currDir);
                }
           }
            CutCopyFile=null;
            CutCopyFlag=2;
            Paste.setEnabled(false);
        }
    }
    
    //Dhmiourgia action listener gia to Paste gia aristero klik h gia deksi klik se xwro opou den uparxoun eikonidia.
    class PasteListener implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
            if(CutCopyFlag==0){
                String fullName = CutCopyFile.getParent();
                fullName = CutCopyFile.toString().replace(fullName, "");
                if(fullName.charAt(0)==File.separatorChar){
                   fullName = fullName.substring(1,fullName.length());
                } 
                File newer= new File(currDir.toString()+File.separatorChar+fullName);
                CutCopyFile.renameTo(newer);
                updateMenu(currDir);
            }
            else if(CutCopyFlag==1){
                boolean isdirectory= CutCopyFile.isDirectory();
                String fullName = CutCopyFile.getParent();
                fullName = CutCopyFile.toString().replace(fullName, "");
                if(fullName.charAt(0)==File.separatorChar){
                   fullName = fullName.substring(1,fullName.length());
                } 
                File newer= new File(currDir.toString()+File.separatorChar+fullName);
                if(!isdirectory){
                    try { 
                    FileReader fin = new FileReader(CutCopyFile);  
                    FileWriter fout = new FileWriter(newer, true);  
                    int c;  
                    while ((c = fin.read()) != -1) {  
                     fout.write(c);  
                    } 

                    fin.close();  
                    fout.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    updateMenu(currDir);
                }
                else{
                    try {
                        copyDirectoryRecursively(CutCopyFile,newer);
                    } catch (IOException ex) {
                        Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    updateMenu(currDir);
                }
           }
            CutCopyFile=null;
            CutCopyFlag=2;
            Paste.setEnabled(false);
        }    
    }
    
    //Dhmiourgia action listener gia to Rename.
    class RenameListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String fileName;
            if(rightClickFlag==1){
               fileName=currRightFile.getName(); 
               String newname = JOptionPane.showInputDialog(dirFiles,"Rename: ",fileName);
               String path= currRightFile.toString().replace(fileName, "");
               if(newname!= null){
                File newer= new File(path+newname);
                boolean b = currRightFile.renameTo(newer);
                updateMenu(currDir);
               }
            }
            else if(leftClickFlag==1){
               fileName=currFile.getName();
               String newname = JOptionPane.showInputDialog(dirFiles,"Rename: ", fileName);
               String path= currFile.toString().replace(fileName, "");
               if(newname!= null){
                File newer= new File(path+newname);
                boolean b = currFile.renameTo(newer);
                updateMenu(currDir);
               }
            }
        }    
    }
    
    //Dhmiourgia action listener gia to Favourites.
    class addToFavouritesListener implements ActionListener{
         public void actionPerformed(ActionEvent e) {
             addToFavourites(currDir);
         }
    }
    
    //Dhmiourgia action listener gia to Properties.
    class PropertiesListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            int n;
            if(rightClickFlag==1){
              long filesize; 
              filesize = calculateSize(currRightFile);
              int sizeoflist = currComp.size();
              for(n=0;n<sizeoflist;n++){
                if(currComp.get(n).getElement1()==currRightFile){
                    break; 
                }
              }
              JOptionPane.showMessageDialog(
                   dirFiles,
                   "Name: "+currComp.get(n).getElement1().getName()+"\n"+"Full Path: "+currComp.get(n).getElement1().toString()+"\n"+"Size: "+filesize+" bytes",
                   "Properties",
                   JOptionPane.INFORMATION_MESSAGE);      
            }
            else if(leftClickFlag==1){
                long filesize;
                filesize = calculateSize(currFile);
                int sizeoflist = currComp.size();
                for(n=0;n<sizeoflist;n++){
                    if(currComp.get(n).getElement1()==currFile){
                        break; 
                    }
                }
              JOptionPane.showMessageDialog(
                   dirFiles,
                   "Name: "+currComp.get(n).getElement1().getName()+"\n"+"Full Path: "+currComp.get(n).getElement1().toString()+"\n"+"Size: "+filesize+" bytes",
                   "Properties",
                   JOptionPane.INFORMATION_MESSAGE);      
            }
        }    
    }
    
    //Dhmiourgia action listener gia to Delete.
    class DeleteListener implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
           int answer = JOptionPane.showConfirmDialog(
                            dirFiles, "Delete ?",
                            "Warning",
                            JOptionPane.YES_NO_OPTION);
            if(answer == JOptionPane.YES_OPTION){
                if(rightClickFlag==1){
                    boolean isdirectory=currRightFile.isDirectory();
                    if(!isdirectory){
                        if(currRightFile.delete()){
                            updateMenu(currDir);
                        }
                        else{
                            JOptionPane.showMessageDialog(
                   dirFiles,
                   "Failed!",
                   "ERROR",
                   JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else{
                        recursiveDeletion(currRightFile);
                        updateMenu(currDir);
                    }
                }
                else if(leftClickFlag==1){
                    boolean isdirectory=currFile.isDirectory();
                    if(!isdirectory){
                        if(currFile.delete()){
                            updateMenu(currDir);
                        }
                        else{
                            JOptionPane.showMessageDialog(
                   dirFiles,
                   "Failed!",
                   "ERROR",
                   JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else{
                        recursiveDeletion(currFile);
                        updateMenu(currDir);
                    }
                }
            }
 
        }    
    }
    
    //Krivei ta krufa arxeia kai fakelous ston trexwn katalogo.
    public void HideHidden(){
        
        int n;
        int sizeoflist = currComp.size();
        for(n=0;n<sizeoflist;n++){
            if(currComp.get(n).getElement0().getText().charAt(0)=='.'){
               currComp.get(n).getElement0().setVisible(false);
            }
        }
                
    }
    
    //Emfanizei ta krufa arxeia kai fakelous ston trexwn katalogo.
    public void ShowHidden(){
        
        int n;
        int sizeoflist = currComp.size();
        
        for(n=0;n<sizeoflist;n++){
            
            if(currComp.get(n).getElement0().getText().charAt(0)=='.'){
               currComp.get(n).getElement0().setVisible(true);
            }
        }
                
    }
    
    //Dhmiourgia item listener gia ta hidden files/folders.
    class HiddenListener implements ItemListener{
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
               ShowHidden(); 
            } else {
               HideHidden();
            }
            updateMenu(currDir);
        }
    }
    
    //Vriskei ta apodekta apotelesmata ths anazhthshs.
    public void searchResults(java.io.File dir, String input, String type){
        
        File[] listOfFiles = dir.listFiles();
        String fullName; 
        String extension="";
            
        for (File file : listOfFiles) {
            
            boolean isdirectory= file.isDirectory();
            if(isdirectory){
               searchResults(file, input, type); 
            }
            fullName = file.getParent();
            fullName = file.toString().replace(fullName, "");
            if(fullName.charAt(0)==File.separatorChar){
               fullName = fullName.substring(1,fullName.length());
            }
            fullName.toLowerCase();
            
            boolean directory= file.isDirectory();
            if(!directory){
             int indexing = fullName.lastIndexOf('.');
             if(indexing!=-1){
              extension= fullName.substring(indexing+1,fullName.length());  
              fullName= fullName.substring(0,indexing);
             }
            }
            if(fullName.contains(input)){
                if(type.equals("")){
                    SearchResults.add(file.toString()); 
                }
                else if(type.equals("dir")){
                    if(directory){
                        SearchResults.add(file.toString());
                    }
                }
                else if(type.equals(extension)){
                    SearchResults.add(file.toString());
                }
            }
        }
        
    }
    
    //Dhmiourgia list selection listener gia ta apotelesmata tou Search.
    class SearchResultsListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) { 
            
            String s = SearchList.getSelectedValue();
            
            File pickedFile= new File(s);
            boolean isDirectory= pickedFile.isDirectory();
            if(!isDirectory){
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(pickedFile);
                } catch (IOException ex) {
                    Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
                updateMenu(currDir);
                BorderLayout layout = (BorderLayout)inBetween.getLayout();
                inBetween.remove(layout.getLayoutComponent(BorderLayout.CENTER));
                inBetween.add(dirFiles, BorderLayout.CENTER);
                revalidate();
            }
            else{
                updateMenu(pickedFile);
                BorderLayout layout = (BorderLayout)inBetween.getLayout();
                inBetween.remove(layout.getLayoutComponent(BorderLayout.CENTER));
                inBetween.add(dirFiles, BorderLayout.CENTER);
                revalidate();
            }
            
        }
    }
    
    //Dhmiourgia action listener gia to Search button.
    class SearchButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e) { 
            
            String type = "";
            String input = searchBar.getText();
            input.toLowerCase();
            SearchResults= new ArrayList<>();
            JPanel SearchPanel = new JPanel();
            
            input = input.replace(" ", "");
            if(input.contains("type:")){
               type= input.substring(input.indexOf("type:"),input.length());
               type= type.replaceAll("type:", "");
               input= input.substring(0,input.indexOf("type:"));
            }
            searchResults(currDir, input, type);
            
            BorderLayout layout = (BorderLayout)inBetween.getLayout();
            inBetween.remove(layout.getLayoutComponent(BorderLayout.CENTER));
            
            int sizeoflist = SearchResults.size();
            String[] myStrings = new String[sizeoflist];
            for(int n=0;n<sizeoflist;n++){
                myStrings[n]=(SearchResults.get(n));
            }
            SearchList = new JList<>(myStrings);
            SearchList.addListSelectionListener(new SearchResultsListener());
            
            JScrollPane scrollingList = new JScrollPane(SearchList);
            scrollingList.setPreferredSize(new Dimension(250,200));
            scrollingList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollingList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            SearchPanel.add(scrollingList);
            SearchList.setLayoutOrientation(JList.VERTICAL);
            inBetween.add(SearchPanel, BorderLayout.CENTER);          
            
            revalidate();
        }
    }
    
}
