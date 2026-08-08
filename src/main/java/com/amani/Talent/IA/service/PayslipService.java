package com.amani.Talent.IA.service;

import com.amani.Talent.IA.dto.PayslipResponse;
import com.amani.Talent.IA.entity.Payslip;
import com.amani.Talent.IA.entity.Payroll;
import com.amani.Talent.IA.repository.PayslipRepository;
import com.amani.Talent.IA.repository.PayrollRepository;

import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.FileInputStream;


@Service
@RequiredArgsConstructor
public class PayslipService {


    private final PayslipRepository payslipRepository;

    private final PayrollRepository payrollRepository;


    private final String PDF_FOLDER =
            System.getProperty("user.dir")
                    + File.separator
                    + "payslips"
                    + File.separator;


    // ==============================
    // COULEURS PDF
    // ==============================

    private static final Color BLUE_PRIMARY = new Color(30, 70, 140);
    private static final Color LIGHT_BLUE = new Color(238, 244, 255);
    private static final Color GREEN_PRIMARY = new Color(40, 130, 60);
    private static final Color GREY_LIGHT = new Color(248, 249, 250);
    private static final Color BORDER_COLOR = new Color(180, 190, 200);
    private static final Color GREY_TEXT = new Color(100, 110, 120);
    private static final Color ROW_STRIP = new Color(240, 245, 255);


    // ==============================
    // CHARGEMENT DU LOGO
    // ==============================

    private Image loadLogo() {
        try {
            // ESSAI 1: Depuis les resources (Spring Boot)
            InputStream logoStream = getClass().getResourceAsStream("/static/images/logo.png");
            if (logoStream != null) {
                BufferedImage img = ImageIO.read(logoStream);
                Image logo = Image.getInstance(img, null);
                logo.scaleToFit(70, 70);
                return logo;
            }

            // ESSAI 2: Depuis le dossier du projet
            String projectPath = System.getProperty("user.dir");
            String[] paths = {
                    projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "images" + File.separator + "logo.png",
                    projectPath + File.separator + "logo.png",
                    projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "logo.png"
            };

            for (String path : paths) {
                File logoFile = new File(path);
                if (logoFile.exists()) {
                    BufferedImage img = ImageIO.read(logoFile);
                    Image logo = Image.getInstance(img, null);
                    logo.scaleToFit(70, 70);
                    System.out.println("✅ Logo chargé depuis : " + path);
                    return logo;
                }
            }

            System.out.println("⚠️ Logo non trouvé - Utilisation du texte");
            return null;

        } catch (Exception e) {
            System.out.println("⚠️ Erreur chargement logo : " + e.getMessage());
            return null;
        }
    }


    // ==============================
    // GENERER FICHE DE PAIE
    // ==============================

    public PayslipResponse generatePayslip(Long payrollId) {

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll introuvable"));

        Payslip payslip = payslipRepository.findByPayrollId(payrollId)
                .orElse(new Payslip());

        String fileName = "payslip_" + payrollId + ".pdf";
        String filePath = PDF_FOLDER + fileName;

        generatePdf(payroll, filePath);

        payslip.setPayroll(payroll);
        payslip.setGeneratedDate(LocalDate.now());
        payslip.setPdfPath(filePath);
        payslip.setStatus("GENERATED");

        payslipRepository.save(payslip);

        return convertToResponse(payslip);
    }


    // ==============================
    // GENERATION PDF - SANS FOOTER ET SANS NOTE CONFIDENTIELLE
    // ==============================

    private void generatePdf(Payroll payroll, String path) {

        try {
            File folder = new File(PDF_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            Document document = new Document(PageSize.A4, 30, 30, 35, 35);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            // SUPPRESSION DU FOOTER
            // writer.setPageEvent(new PdfFooter());

            document.open();

            // ==============================
            // POLICES
            // ==============================

            Font fontBold20 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BLUE_PRIMARY);
            Font fontBold16 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fontBold14 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontBold13 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
            Font fontBold12 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font fontNormal11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fontWhite16 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.WHITE);
            Font fontWhite13 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.WHITE);
            Font fontWhite11 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

            // ==============================
            // ENTÊTE - AVEC LOGO
            // ==============================

            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{0.7f, 3f, 1.5f});
            headerTable.setSpacingAfter(10);

            // Cellule Logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setPadding(0);

            Image logo = loadLogo();
            if (logo != null) {
                logo.setAlignment(Image.ALIGN_LEFT);
                logoCell.addElement(logo);
            } else {
                Paragraph logoText = new Paragraph("T A",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, BLUE_PRIMARY));
                logoText.setAlignment(Element.ALIGN_LEFT);
                logoCell.addElement(logoText);
            }
            headerTable.addCell(logoCell);

            // Cellule Nom entreprise
            PdfPCell companyCell = new PdfPCell();
            companyCell.setBorder(Rectangle.NO_BORDER);
            companyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph companyName = new Paragraph("TALENT IA", fontBold20);
            companyName.setAlignment(Element.ALIGN_LEFT);
            companyCell.addElement(companyName);

            Paragraph companyInfo = new Paragraph("Solution RH & Gestion des talents", fontNormal12);
            companyInfo.setAlignment(Element.ALIGN_LEFT);
            companyInfo.getFont().setColor(GREY_TEXT);
            companyCell.addElement(companyInfo);

            Paragraph companyContact = new Paragraph("Tunisie - contact@talentia.com", fontNormal11);
            companyContact.setAlignment(Element.ALIGN_LEFT);
            companyContact.getFont().setColor(GREY_TEXT);
            companyCell.addElement(companyContact);

            headerTable.addCell(companyCell);

            // Cellule Bulletin de paie
            PdfPCell refCell = new PdfPCell();
            refCell.setBorder(Rectangle.NO_BORDER);
            refCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            refCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            PdfPTable refTable = new PdfPTable(1);
            refTable.setWidthPercentage(100);

            PdfPCell refTitleCell = new PdfPCell(new Phrase("BULLETIN DE PAIE", fontWhite16));
            refTitleCell.setBackgroundColor(BLUE_PRIMARY);
            refTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            refTitleCell.setPadding(8);
            refTitleCell.setBorder(Rectangle.BOX);
            refTitleCell.setBorderColor(BLUE_PRIMARY);
            refTable.addCell(refTitleCell);

            refCell.addElement(refTable);

            Paragraph refNumber = new Paragraph("N° " + payroll.getId(), fontNormal11);
            refNumber.setAlignment(Element.ALIGN_RIGHT);
            refNumber.setSpacingBefore(3);
            refCell.addElement(refNumber);

            headerTable.addCell(refCell);

            document.add(headerTable);

            addSeparator(document);

            // ==============================
            // INFORMATIONS EMPLOYÉ
            // ==============================

            String employeeName = payroll.getEmployee().getUser().getName()
                    + " " + payroll.getEmployee().getUser().getLastname();
            String monthYear = getMonthName(payroll.getMonth()) + " " + payroll.getYear();

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.2f, 2.8f});
            infoTable.setSpacingBefore(8);
            infoTable.setSpacingAfter(8);

            addInfoRow(infoTable, "Matricule", payroll.getEmployee().getEmployeeCode(), LIGHT_BLUE);
            addInfoRow(infoTable, "Période", monthYear, LIGHT_BLUE);
            addInfoRow(infoTable, "Nom complet", employeeName, LIGHT_BLUE);
            addInfoRow(infoTable, "Département", payroll.getEmployee().getDepartment(), LIGHT_BLUE);
            addInfoRow(infoTable, "Poste", payroll.getEmployee().getPosition(), LIGHT_BLUE);
            addInfoRow(infoTable, "Statut", "Personnel Mensuel", LIGHT_BLUE);

            document.add(infoTable);

            addSeparator(document);

            // ==============================
            // TABLEAU DES SALAIRES
            // ==============================

            PdfPTable salaryTable = new PdfPTable(new float[]{0.8f, 3f, 2.2f, 2.2f});
            salaryTable.setWidthPercentage(100);
            salaryTable.setSpacingBefore(8);
            salaryTable.setSpacingAfter(10);

            addSalaryHeader(salaryTable);

            BigDecimal salaireBrut = payroll.getBaseSalary()
                    .add(payroll.getBonus())
                    .add(payroll.getOvertime());

            BigDecimal cnss = payroll.getBaseSalary()
                    .multiply(new BigDecimal("0.092"))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            addSalaryRow(salaryTable, "100", "Salaire de base", payroll.getBaseSalary(), BigDecimal.ZERO, false);
            addSalaryRow(salaryTable, "110", "Prime", payroll.getBonus(), BigDecimal.ZERO, false);
            addSalaryRow(salaryTable, "120", "Heures supplémentaires", payroll.getOvertime(), BigDecimal.ZERO, false);
            addSalaryRow(salaryTable, "200", "Salaire brut", salaireBrut, BigDecimal.ZERO, true);
            addSalaryRow(salaryTable, "210", "CNSS", BigDecimal.ZERO, cnss, false);
            addSalaryRow(salaryTable, "250", "Impôts", BigDecimal.ZERO, payroll.getDeduction(), false);
            addSalaryRow(salaryTable, "500", "NET À PAYER", payroll.getNetSalary(), BigDecimal.ZERO, true);

            document.add(salaryTable);

            // ==============================
            // BLOC NET À PAYER + CONGÉS + PAIEMENT
            // ==============================

            PdfPTable bottomTable = new PdfPTable(3);
            bottomTable.setWidthPercentage(100);
            bottomTable.setWidths(new float[]{1.8f, 1.2f, 1.2f});
            bottomTable.setSpacingBefore(12);
            bottomTable.setSpacingAfter(12);

            // 1. Net à payer
            PdfPCell netCell = new PdfPCell();
            netCell.setBackgroundColor(GREEN_PRIMARY);
            netCell.setPadding(15);
            netCell.setBorder(Rectangle.NO_BORDER);
            netCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph netLabel = new Paragraph("NET À PAYER", fontWhite13);
            netLabel.setAlignment(Element.ALIGN_CENTER);
            netCell.addElement(netLabel);

            Font fontNetAmount = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, Color.WHITE);
            Paragraph netAmount = new Paragraph(
                    payroll.getNetSalary().setScale(2).toString() + " DT",
                    fontNetAmount
            );
            netAmount.setAlignment(Element.ALIGN_CENTER);
            netAmount.setSpacingBefore(5);
            netCell.addElement(netAmount);

            bottomTable.addCell(netCell);

            // 2. Congés
            PdfPCell leaveCell = new PdfPCell();
            leaveCell.setBackgroundColor(GREY_LIGHT);
            leaveCell.setPadding(12);
            leaveCell.setBorder(Rectangle.NO_BORDER);
            leaveCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph leaveTitle = new Paragraph("CONGÉS", fontBold14);
            leaveTitle.setAlignment(Element.ALIGN_CENTER);
            leaveCell.addElement(leaveTitle);

            String leaveInfo = "Acquis : 0 jours\nPris : 0 jours\nSolde : 0 jours";
            Paragraph leaveText = new Paragraph(leaveInfo, fontNormal12);
            leaveText.setAlignment(Element.ALIGN_CENTER);
            leaveText.setLeading(18);
            leaveText.setSpacingBefore(5);
            leaveCell.addElement(leaveText);

            bottomTable.addCell(leaveCell);

            // 3. Paiement
            PdfPCell paymentCell = new PdfPCell();
            paymentCell.setBackgroundColor(GREY_LIGHT);
            paymentCell.setPadding(12);
            paymentCell.setBorder(Rectangle.NO_BORDER);
            paymentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph paymentTitle = new Paragraph("PAIEMENT", fontBold14);
            paymentTitle.setAlignment(Element.ALIGN_CENTER);
            paymentCell.addElement(paymentTitle);

            Paragraph paymentInfo = new Paragraph("Mode : Banque\nCompte : BIAT", fontNormal12);
            paymentInfo.setAlignment(Element.ALIGN_CENTER);
            paymentInfo.setLeading(18);
            paymentInfo.setSpacingBefore(5);
            paymentCell.addElement(paymentInfo);

            bottomTable.addCell(paymentCell);

            document.add(bottomTable);

            addSeparator(document);

            // ==============================
            // SIGNATURES
            // ==============================

            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setWidths(new float[]{1, 1});
            signatureTable.setSpacingBefore(8);

            addSignatureCell(signatureTable, "Signature employé");
            addSignatureCell(signatureTable, "Responsable RH");

            document.add(signatureTable);

            // ==============================
            // NOTE CONFIDENTIELLE - SUPPRIMÉE
            // ==============================

            // La note "Ce bulletin de paie est un document confidentiel." a été supprimée

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF : " + e.getMessage());
        }
    }


    // ==============================
    // MÉTHODES UTILITAIRES
    // ==============================

    private void addInfoRow(PdfPTable table, String label, String value, Color bgColor) {
        PdfPCell labelCell = new PdfPCell(
                new Phrase(label + " :", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))
        );
        labelCell.setBackgroundColor(bgColor);
        labelCell.setPadding(7);
        labelCell.setBorderWidth(0.5f);
        labelCell.setBorderColor(BORDER_COLOR);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell valueCell = new PdfPCell(
                new Phrase(value != null ? value : "", FontFactory.getFont(FontFactory.HELVETICA, 12))
        );
        valueCell.setPadding(7);
        valueCell.setBorderWidth(0.5f);
        valueCell.setBorderColor(BORDER_COLOR);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addSalaryHeader(PdfPTable table) {
        String[] headers = {"Code", "Libellé", "Gains", "Retenues"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(
                    new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE))
            );
            cell.setBackgroundColor(BLUE_PRIMARY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addSalaryRow(PdfPTable table, String code, String label,
                              BigDecimal gain, BigDecimal retenue, boolean isBold) {
        Font font = isBold ?
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12) :
                FontFactory.getFont(FontFactory.HELVETICA, 12);

        Color bgColor = isBold ? ROW_STRIP : Color.WHITE;

        PdfPCell codeCell = new PdfPCell(new Phrase(code, font));
        codeCell.setBackgroundColor(bgColor);
        codeCell.setPadding(6);
        codeCell.setBorderWidth(0.5f);
        codeCell.setBorderColor(BORDER_COLOR);
        codeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(codeCell);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBackgroundColor(bgColor);
        labelCell.setPadding(6);
        labelCell.setBorderWidth(0.5f);
        labelCell.setBorderColor(BORDER_COLOR);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        PdfPCell gainCell = new PdfPCell(
                new Phrase(gain.setScale(2).toString() + " DT", font)
        );
        gainCell.setBackgroundColor(bgColor);
        gainCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        gainCell.setPadding(6);
        gainCell.setBorderWidth(0.5f);
        gainCell.setBorderColor(BORDER_COLOR);
        gainCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(gainCell);

        PdfPCell retenueCell = new PdfPCell(
                new Phrase(retenue.setScale(2).toString() + " DT", font)
        );
        retenueCell.setBackgroundColor(bgColor);
        retenueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        retenueCell.setPadding(6);
        retenueCell.setBorderWidth(0.5f);
        retenueCell.setBorderColor(BORDER_COLOR);
        retenueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(retenueCell);
    }

    private void addSignatureCell(PdfPTable table, String label) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);

        Paragraph labelPar = new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        labelPar.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(labelPar);

        Paragraph line = new Paragraph("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _",
                FontFactory.getFont(FontFactory.HELVETICA, 12));
        line.setAlignment(Element.ALIGN_LEFT);
        line.setSpacingBefore(5);
        cell.addElement(line);

        table.addCell(cell);
    }

    private void addSeparator(Document document) throws DocumentException {
        Paragraph sep = new Paragraph(" ");
        sep.setSpacingBefore(5);
        sep.setSpacingAfter(5);
        document.add(sep);
    }

    private String getMonthName(int month) {
        String[] months = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        return month >= 1 && month <= 12 ? months[month - 1] : String.valueOf(month);
    }


    // ==============================
    // LISTE DES FICHES
    // ==============================

    public List<PayslipResponse> getAllPayslips() {
        return payslipRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    // ==============================
    // CONVERSION RESPONSE
    // ==============================

    private PayslipResponse convertToResponse(Payslip payslip) {
        PayslipResponse response = new PayslipResponse();
        Payroll payroll = payslip.getPayroll();

        response.setId(payslip.getId());
        response.setEmployeeName(
                payroll.getEmployee().getUser().getName() + " " +
                        payroll.getEmployee().getUser().getLastname()
        );
        response.setMonth(payroll.getMonth());
        response.setYear(payroll.getYear());
        response.setBaseSalary(payroll.getBaseSalary());
        response.setBonus(payroll.getBonus());
        response.setOvertime(payroll.getOvertime());
        response.setDeduction(payroll.getDeduction());
        response.setNetSalary(payroll.getNetSalary());
        response.setGeneratedDate(payslip.getGeneratedDate());
        response.setPdfPath(payslip.getPdfPath());
        response.setStatus(payslip.getStatus());

        return response;
    }

}