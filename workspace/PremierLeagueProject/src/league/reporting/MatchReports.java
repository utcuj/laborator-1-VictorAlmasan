package league.reporting;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import static net.sf.dynamicreports.report.builder.DynamicReports.field;

import java.awt.Color;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

import league.project.model.DBConnectionPool;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import net.sf.dynamicreports.examples.Templates;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;

public class MatchReports {

	public static void matchList() {

		try (Connection co = DBConnectionPool.getInstance().getConnection()) {

			ImageBuilder image = cmp.image(new ImageExpression()).setFixedDimension(14, 14);

			StyleBuilder boldStyle = stl.style().bold();
			StyleBuilder coloredStyle = stl.style(boldStyle).setForegroundColor(new Color(0,0,255));
			StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
			StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point())
					.setBackgroundColor(new Color(255,69,0)).setForegroundColor(Color.white);

			StyleBuilder titleStyle = stl.style(boldCenteredStyle).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFontSize(15);

			// definire coloane

			TextColumnBuilder<String> round = col.column("Round", "round_code", type.stringType())
					.setStyle(coloredStyle);
			TextColumnBuilder<String> home = col.column("Home Team", "home_team", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<String> away = col.column("Away Team", "away_team", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Date> date = col.column("Date", "match_date", type.dateType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER).setFixedWidth(100);
			TextColumnBuilder<String> score = col.column("Score", "score", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER).setFixedWidth(50);

			// raport nou
			JasperReportBuilder report = DynamicReports.report();
			// report.setTemplate(Templates.reportTemplate);
			report.setColumnTitleStyle(columnTitleStyle);

			// colorare randuri pare/impare
			report.setDetailEvenRowStyle(stl.simpleStyle().setBackgroundColor(new Color(245,222,179)));
			report.setDetailOddRowStyle(stl.simpleStyle().setBackgroundColor(new Color(255,250,205)));
			report.setHighlightDetailEvenRows(true);
			report.setHighlightDetailOddRows(true);

			report.title(
					cmp.horizontalList()
							.add(cmp.image("images/logo.png").setFixedDimension(90, 100),
									cmp.text("Football Match List").setStyle(titleStyle)
											.setHorizontalAlignment(HorizontalAlignment.RIGHT))
							.newRow()
							.add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)),
					cmp.horizontalList().add(cmp.text("Match Statuses:"),
							cmp.image("images/redCLegend.png").setFixedDimension(7, 7), cmp.text("Bankruptcy"),
							cmp.image("images/greenCLegend.png").setFixedDimension(7, 7), cmp.text("Finished"),
							cmp.image("images/blueCLegend.png").setFixedDimension(7, 7), cmp.text("Pending"),
							cmp.image("images/orangeCLegend.png").setFixedDimension(7, 7), cmp.text("Unscheduled")),
					cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10));

			report.fields(field("match_status_code", String.class));
			// adaugare coloane
			report.columns(round, home, away, date, score, col.componentColumn("", image));
			report.groupBy(round);

			// afisare nr paginii in josul paginii
			report.pageFooter(Components.pageXofY());
			// colectarea datelor din baza de date
			report.setDataSource(
					"SELECT home_team, away_team, match_status_code, match_date, round_code, score FROM matchList ORDER BY round_code ASC, match_status_code",
					co);

			try {
				// afisare raport
				report.show(false);
			} catch (DRException e) {
				AlertDialog.showError("Internal Error", "Generating report failed because: "+e.getMessage());
			}

		} catch (Exception e) {
			AlertDialog.showError("System Error (Reporting)",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
		}
	}

	static class ImageExpression extends AbstractSimpleExpression<InputStream> {
		private static final long serialVersionUID = 1L;

		@Override
		public InputStream evaluate(ReportParameters reportParameters) {
			String imgPath = ApplicationData.MATCH_FINISHED
					.equalsIgnoreCase(reportParameters.getValue("match_status_code"))
							? "/images/greenC.png"
							: ApplicationData.MATCH_BANKRUPTCY
									.equalsIgnoreCase(reportParameters.getValue("match_status_code"))
											? "/images/redC.png"
											: ApplicationData.MATCH_PENDING
													.equalsIgnoreCase(reportParameters.getValue("match_status_code"))
															? "/images/blueC.png" : "/images/orangeC.png";
			return Templates.class.getResourceAsStream(imgPath);
		}

	}

}
