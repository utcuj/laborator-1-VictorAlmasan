package league.reporting;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.exp;
import static net.sf.dynamicreports.report.builder.DynamicReports.field;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

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
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;

public class ContractReports {

	public static void playerContractHistory() {

		try (Connection co = DBConnectionPool.getInstance().getConnection()) {

			ImageBuilder image = cmp.image(new ImageExpression()).setFixedDimension(14, 14);

			StyleBuilder boldStyle = stl.style().bold();
			StyleBuilder coloredStyle = stl.style(boldStyle).setForegroundColor(new Color(0, 0, 153));
			StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
			StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point())
					.setBackgroundColor(new Color(0, 76, 153)).setForegroundColor(Color.white);

			StyleBuilder titleStyle = stl.style(boldCenteredStyle).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFontSize(15);

			// definire coloane

			TextColumnBuilder<String> player = col.column("Player", "player_name", type.stringType())
					.setStyle(coloredStyle);
			TextColumnBuilder<String> team = col.column("Team", "team_name", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Date> start = col.column("Start Date", "start_date", type.dateType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER).setFixedWidth(70);
			TextColumnBuilder<Date> end = col.column("End Date", "end_date", type.dateType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER).setFixedWidth(70);
			TextColumnBuilder<Integer> weeks = col.column("Played Weeks", "weeks", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER).setFixedWidth(80);
			TextColumnBuilder<Integer> salary = col.column("Total Paid (\u00a3)", "total_salary", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER).setFixedWidth(140);


			// raport nou
			JasperReportBuilder report = DynamicReports.report();
			// report.setTemplate(Templates.reportTemplate);
			report.setColumnTitleStyle(columnTitleStyle);

			// subtotals
			report.subtotalsAtSummary(sbt.sum(weeks), sbt.sum(salary));
			report.subtotalsAtFirstGroupFooter(sbt.sum(weeks), sbt.sum(salary));

			// colorare randuri pare/impare
			report.setDetailEvenRowStyle(stl.simpleStyle().setBackgroundColor(new Color(218, 233, 243)));
			report.setDetailOddRowStyle(stl.simpleStyle().setBackgroundColor(new Color(224, 255, 255)));
			report.setHighlightDetailEvenRows(true);
			report.setHighlightDetailOddRows(true);

			report.title(
					cmp.horizontalList()
							.add(cmp.image("images/logo.png").setFixedDimension(90, 100),
									cmp.text("Player Contract History").setStyle(titleStyle)
											.setHorizontalAlignment(HorizontalAlignment.RIGHT))
							.newRow()
							.add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)),
					cmp.horizontalList().add(cmp.text("Contract Statuses:"),
							cmp.image("images/redCLegend.png").setFixedDimension(7, 7), cmp.text("Bankruptcy"),
							cmp.image("images/greenCLegend.png").setFixedDimension(7, 7), cmp.text("Terminate"),
							cmp.image("images/blueCLegend.png").setFixedDimension(7, 7), cmp.text("Renewed"),
							cmp.image("images/orangeCLegend.png").setFixedDimension(7, 7), cmp.text("Cancelled")),
					cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10));

			report.fields(field("status_code", String.class));
			// adaugare coloane
			report.columns(player, team, start, end, weeks, salary, col.componentColumn("", image));
			report.groupBy(player);

			// afisare nr paginii in josul paginii
			report.pageFooter(Components.pageXofY());
			// colectarea datelor din baza de date
			report.setDataSource(
					"SELECT player_name, team_name, weeks, total_salary, status_code, start_date, end_date FROM playerContractHistory ORDER BY player_name ASC,status_code",
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
			String imgPath = ApplicationData.CONTRACT_TERMINATE.equalsIgnoreCase(reportParameters.getValue("status_code"))
					? "/images/greenC.png"
					: ApplicationData.CONTRACT_BANKRUPTCY.equalsIgnoreCase(reportParameters.getValue("status_code"))
							? "/images/redC.png"
							: ApplicationData.CONTRACT_RENEWED
									.equalsIgnoreCase(reportParameters.getValue("status_code"))
											? "/images/blueC.png" : "/images/orangeC.png";
			return Templates.class.getResourceAsStream(imgPath);
		}

	}

}
