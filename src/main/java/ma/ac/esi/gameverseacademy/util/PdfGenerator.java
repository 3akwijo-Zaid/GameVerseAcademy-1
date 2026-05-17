package ma.ac.esi.gameverseacademy.util;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import ma.ac.esi.gameverseacademy.model.GameCard;
import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.model.Payment;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class PdfGenerator {
    private static final DeviceRgb DARK = new DeviceRgb(10, 14, 26);
    private static final DeviceRgb ACCENT = new DeviceRgb(0, 212, 255);
    private static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb GRAY = new DeviceRgb(100, 116, 139);
    private static final DeviceRgb GREEN = new DeviceRgb(16, 185, 129);

    public static byte[] generateReceipt(GameCard card, Payment payment) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph("GAMEVERSE ACADEMY")
                .setFontSize(24).setBold().setFontColor(ACCENT).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Recu de Paiement")
                .setFontSize(14).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));
            doc.add(new Paragraph("REF: " + payment.getTransactionRef())
                .setFontSize(10).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER).setMarginBottom(30));
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
            addRow(table, "Jeu / Mod", card.getModTitle());
            addRow(table, "Acheteur", card.getUserLogin());
            addRow(table, "Titulaire carte", card.getCardHolder());
            addRow(table, "Carte", "**** **** **** " + card.getCardNumberLast4());
            addRow(table, "Date", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(payment.getPaymentDate()));
            addRow(table, "Statut", payment.getStatus());
            doc.add(table);
            doc.add(new Paragraph(String.format("TOTAL: %.2f EUR", payment.getAmount()))
                .setFontSize(20).setBold().setFontColor(GREEN).setTextAlignment(TextAlignment.CENTER).setMarginTop(30));
            doc.add(new Paragraph("Merci pour votre achat sur GameVerse Academy!")
                .setFontSize(10).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER).setMarginTop(40));
            doc.close();
        } catch (Exception e) { e.printStackTrace(); }
        return baos.toByteArray();
    }

    public static byte[] generateModFiche(Mod mod, double avgRating, int totalRatings) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph("GAMEVERSE ACADEMY")
                .setFontSize(24).setBold().setFontColor(ACCENT).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Fiche Technique du Jeu")
                .setFontSize(14).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER).setMarginBottom(30));
            doc.add(new Paragraph(mod.getTitle())
                .setFontSize(22).setBold().setFontColor(WHITE).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
            addRow(table, "Categorie", mod.getCategory() != null ? mod.getCategory() : "-");
            addRow(table, "Auteur / Editeur", mod.getAuthor() != null ? mod.getAuthor() : "-");
            addRow(table, "Publisher", mod.getPublisher() != null ? mod.getPublisher() : "-");
            addRow(table, "Plateforme", mod.getPlatform() != null ? mod.getPlatform() : "-");
            addRow(table, "Date de sortie", mod.getReleaseDate() != null ? mod.getReleaseDate() : "-");
            addRow(table, "Score Metacritic", String.valueOf(mod.getMetacritic()));
            addRow(table, "Telechargements", String.format("%,d", mod.getDownloads()));
            addRow(table, "Prix", String.format("%.2f EUR", mod.getPrice()));
            addRow(table, "Note moyenne", String.format("%.1f / 5 (%d avis)", avgRating, totalRatings));
            doc.add(table);
            if (mod.getDescription() != null && !mod.getDescription().isEmpty()) {
                doc.add(new Paragraph("Description").setFontSize(14).setBold().setFontColor(ACCENT).setMarginTop(20));
                doc.add(new Paragraph(mod.getDescription()).setFontSize(11).setFontColor(GRAY));
            }
            doc.add(new Paragraph("GameVerse Academy - " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
                .setFontSize(9).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER).setMarginTop(40));
            doc.close();
        } catch (Exception e) { e.printStackTrace(); }
        return baos.toByteArray();
    }

    public static byte[] generateDownloadReport(List<Mod> mods) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            doc.add(new Paragraph("GAMEVERSE ACADEMY")
                .setFontSize(24).setBold().setFontColor(ACCENT).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Rapport des Telechargements")
                .setFontSize(14).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Genere le " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()))
                .setFontSize(10).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER).setMarginBottom(30));
            Table table = new Table(UnitValue.createPercentArray(new float[]{5, 30, 20, 15, 15, 15})).useAllAvailableWidth();
            String[] headers = {"#", "Titre", "Categorie", "Publisher", "Metacritic", "Downloads"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setBold().setFontColor(ACCENT)).setBackgroundColor(DARK));
            }
            int total = 0;
            for (int i = 0; i < mods.size(); i++) {
                Mod m = mods.get(i);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
                table.addCell(new Cell().add(new Paragraph(m.getTitle() != null ? m.getTitle() : "")));
                table.addCell(new Cell().add(new Paragraph(m.getCategory() != null ? m.getCategory() : "")));
                table.addCell(new Cell().add(new Paragraph(m.getPublisher() != null ? m.getPublisher() : "")));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(m.getMetacritic()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%,d", m.getDownloads()))));
                total += m.getDownloads();
            }
            doc.add(table);
            doc.add(new Paragraph("Total telechargements : " + String.format("%,d", total))
                .setFontSize(14).setBold().setFontColor(GREEN).setMarginTop(20));
            doc.close();
        } catch (Exception e) { e.printStackTrace(); }
        return baos.toByteArray();
    }

    private static void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold().setFontColor(GRAY)));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "")));
    }
}
