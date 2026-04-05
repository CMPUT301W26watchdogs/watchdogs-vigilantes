// generating a PDF confirmation ticket for entrants who got accepted into an event after the lottery draw (Wildcard)

package com.example.vigilante;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

// using Android's built in PdfDocument API so no external libraries are needed
public class TicketGenerator {

    // ticket page dimensions in postscript points (roughly 6x3 inches for a ticket stub look)
    static final int PAGE_WIDTH = 432;
    static final int PAGE_HEIGHT = 216;

    // holding all the event info needed to fill in the ticket
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private String attendeeName;
    private String ticketId;

    /**
     * creating a new ticket generator with the event and attendee info
     * that will be printed on the PDF ticket
     *
     * @param eventTitle the name of the event
     * @param eventDate the date string shown on the ticket
     * @param eventLocation the venue or location text
     * @param attendeeName the name of the person attending
     * @param ticketId a unique identifier printed on the ticket stub
     */
    public TicketGenerator(String eventTitle, String eventDate, String eventLocation,
                           String attendeeName, String ticketId) {
        this.eventTitle = eventTitle != null ? eventTitle : "Untitled Event";
        this.eventDate = eventDate != null ? eventDate : "TBD";
        this.eventLocation = eventLocation != null ? eventLocation : "TBD";
        this.attendeeName = attendeeName != null ? attendeeName : "Attendee";
        this.ticketId = ticketId != null ? ticketId : "000000";
    }

    /**
     * building a single page PDF document containing the fully drawn ticket
     * with event details, attendee name and a tear off stub
     *
     * @return the completed PdfDocument ready to be written to a file
     */
    // building the full PDF document with the ticket drawn on a single page
    // Citation: Ved, March 16 2025, Claude referred to https://developer.android.com/reference/android/graphics/pdf/PdfDocument
    public PdfDocument generate() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        drawTicket(canvas);

        document.finishPage(page);
        return document;
    }

    /**
     * drawing the complete ticket layout including outer border,
     * dashed tear line and both the main and stub sections
     *
     * @param canvas the canvas to draw the ticket on
     */
    // drawing the entire ticket layout on the canvas with border, dashed tear line and event details
    private void drawTicket(Canvas canvas) {
        // filling background white
        canvas.drawColor(Color.WHITE);

        // drawing outer border with rounded feel
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#333333"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);
        canvas.drawRect(8, 8, PAGE_WIDTH - 8, PAGE_HEIGHT - 8, borderPaint);

        // drawing dashed vertical tear line separating the main section from the stub
        Paint dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setColor(Color.parseColor("#AAAAAA"));
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(1f);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{6, 4}, 0));
        float tearX = PAGE_WIDTH * 0.72f;
        canvas.drawLine(tearX, 16, tearX, PAGE_HEIGHT - 16, dashPaint);

        // drawing the main ticket section on the left side
        drawMainSection(canvas, tearX);

        // drawing the stub section on the right side
        drawStubSection(canvas, tearX);
    }

    /**
     * drawing the left side of the ticket with the brand name, event title,
     * attendee name, date, location and confirmation status
     *
     * @param canvas the canvas to draw on
     * @param tearX the x coordinate of the dashed tear line separating main from stub
     */
    // drawing event title, attendee name, date and location on the left portion of the ticket
    private void drawMainSection(Canvas canvas, float tearX) {
        float leftMargin = 24;
        float contentWidth = tearX - leftMargin - 16;

        // brand name at the top
        Paint brandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        brandPaint.setColor(Color.parseColor("#C62828"));
        brandPaint.setTextSize(11);
        brandPaint.setFakeBoldText(true);
        brandPaint.setLetterSpacing(0.15f);
        canvas.drawText("VIGILANTE", leftMargin, 32, brandPaint);

        // event title large and bold
        Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setColor(Color.parseColor("#1A1A1A"));
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true);

        // truncating the title if it overflows the available width
        String displayTitle = truncateText(eventTitle, titlePaint, contentWidth);
        canvas.drawText(displayTitle, leftMargin, 62, titlePaint);

        // thin separator line under the title
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#E0E0E0"));
        linePaint.setStrokeWidth(1f);
        canvas.drawLine(leftMargin, 72, tearX - 16, 72, linePaint);

        // detail labels and values
        Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.parseColor("#888888"));
        labelPaint.setTextSize(8);
        labelPaint.setFakeBoldText(true);
        labelPaint.setLetterSpacing(0.1f);

        Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.parseColor("#333333"));
        valuePaint.setTextSize(11);

        // attendee name row
        canvas.drawText("ATTENDEE", leftMargin, 92, labelPaint);
        canvas.drawText(truncateText(attendeeName, valuePaint, contentWidth), leftMargin, 106, valuePaint);

        // date row
        canvas.drawText("DATE", leftMargin, 126, labelPaint);
        canvas.drawText(truncateText(eventDate, valuePaint, contentWidth / 2), leftMargin, 140, valuePaint);

        // location row next to date
        float locX = leftMargin + contentWidth * 0.5f;
        canvas.drawText("LOCATION", locX, 126, labelPaint);
        canvas.drawText(truncateText(eventLocation, valuePaint, contentWidth / 2 - 8), locX, 140, valuePaint);

        // confirmation status at the bottom
        Paint confirmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        confirmPaint.setColor(Color.parseColor("#2E7D32"));
        confirmPaint.setTextSize(10);
        confirmPaint.setFakeBoldText(true);
        canvas.drawText("CONFIRMED", leftMargin, 170, confirmPaint);

        // ticket ID at the bottom right of the main section
        Paint idPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        idPaint.setColor(Color.parseColor("#AAAAAA"));
        idPaint.setTextSize(7);
        String shortId = ticketId.length() > 8 ? ticketId.substring(0, 8).toUpperCase() : ticketId.toUpperCase();
        canvas.drawText("#" + shortId, leftMargin, 190, idPaint);
    }

    /**
     * drawing the right side stub with event name, date, location
     * and an "admit one" label for tearing off
     *
     * @param canvas the canvas to draw on
     * @param tearX the x coordinate where the stub section begins
     */
    // drawing the small stub on the right side with rotated event info for tearing off
    private void drawStubSection(Canvas canvas, float tearX) {
        float stubCenter = tearX + (PAGE_WIDTH - tearX) / 2f;

        // brand on the stub
        Paint stubBrandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stubBrandPaint.setColor(Color.parseColor("#C62828"));
        stubBrandPaint.setTextSize(9);
        stubBrandPaint.setFakeBoldText(true);
        stubBrandPaint.setLetterSpacing(0.12f);
        stubBrandPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("VIGILANTE", stubCenter, 32, stubBrandPaint);

        // event title on the stub
        Paint stubTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stubTitlePaint.setColor(Color.parseColor("#1A1A1A"));
        stubTitlePaint.setTextSize(10);
        stubTitlePaint.setFakeBoldText(true);
        stubTitlePaint.setTextAlign(Paint.Align.CENTER);

        float stubWidth = PAGE_WIDTH - tearX - 24;
        String stubTitle = truncateText(eventTitle, stubTitlePaint, stubWidth);
        canvas.drawText(stubTitle, stubCenter, 56, stubTitlePaint);

        // date on the stub
        Paint stubDetailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stubDetailPaint.setColor(Color.parseColor("#666666"));
        stubDetailPaint.setTextSize(9);
        stubDetailPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(eventDate, stubCenter, 78, stubDetailPaint);

        // location on the stub
        String stubLoc = truncateText(eventLocation, stubDetailPaint, stubWidth);
        canvas.drawText(stubLoc, stubCenter, 96, stubDetailPaint);

        // admit one text
        Paint admitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        admitPaint.setColor(Color.parseColor("#C62828"));
        admitPaint.setTextSize(8);
        admitPaint.setFakeBoldText(true);
        admitPaint.setLetterSpacing(0.2f);
        admitPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("ADMIT ONE", stubCenter, 140, admitPaint);

        // ticket number on the stub
        Paint stubIdPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stubIdPaint.setColor(Color.parseColor("#AAAAAA"));
        stubIdPaint.setTextSize(7);
        stubIdPaint.setTextAlign(Paint.Align.CENTER);
        String shortId = ticketId.length() > 8 ? ticketId.substring(0, 8).toUpperCase() : ticketId.toUpperCase();
        canvas.drawText("#" + shortId, stubCenter, 190, stubIdPaint);
    }

    /**
     * shortening text by trimming characters from the end and appending
     * an ellipsis until it fits within the given pixel width
     *
     * @param text the string to truncate
     * @param paint the Paint used to measure text width
     * @param maxWidth the maximum allowed width in pixels
     * @return the original text if it fits, or a truncated version with "..."
     */
    // shortening text with an ellipsis if it exceeds the max width in pixels
    // Citation: Ved, March 16 2025, Claude referred to https://developer.android.com/reference/android/graphics/Paint#measureText(java.lang.String)
    static String truncateText(String text, Paint paint, float maxWidth) {
        if (text == null) return "";
        if (paint.measureText(text) <= maxWidth) return text;

        // trimming characters from the end until it fits with an ellipsis
        for (int i = text.length() - 1; i > 0; i--) {
            String trimmed = text.substring(0, i) + "...";
            if (paint.measureText(trimmed) <= maxWidth) {
                return trimmed;
            }
        }
        return "...";
    }

    // getters so tests can verify the ticket data
    public String getEventTitle() { return eventTitle; }
    public String getEventDate() { return eventDate; }
    public String getEventLocation() { return eventLocation; }
    public String getAttendeeName() { return attendeeName; }
    public String getTicketId() { return ticketId; }
}
