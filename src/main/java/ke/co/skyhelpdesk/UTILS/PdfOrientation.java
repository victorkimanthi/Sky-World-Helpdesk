package ke.co.skyhelpdesk.UTILS;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import javax.swing.text.Document;
import java.math.BigInteger;

public class PdfOrientation {
    public static void changePdfOrientation(Document document, String orientation){
        CTDocument1 doc = (CTDocument1) document;
        CTBody body = doc.getBody();
        CTSectPr section = body.addNewSectPr();
        XWPFDocument dummy=new XWPFDocument();
        XWPFParagraph para = dummy.createParagraph();
        CTP ctp = para.getCTP();
        CTPPr br = ctp.addNewPPr();
        br.setSectPr(section);
        CTPageSz pageSize = section.getPgSz();
        System.out.println("doc:"+doc);
        System.out.println("body:"+body);
        System.out.println("section:"+section);
        System.out.println("pageSize:"+pageSize);
        if(orientation.equals("landscape")){
            pageSize.setOrient(STPageOrientation.LANDSCAPE);
            pageSize.setW(BigInteger.valueOf(842 * 20));
            pageSize.setH(BigInteger.valueOf(595 * 20));
        }
        else{
            pageSize.setOrient(STPageOrientation.PORTRAIT);
            pageSize.setH(BigInteger.valueOf(842 * 20));
            pageSize.setW(BigInteger.valueOf(595 * 20));
        }
    }
}
