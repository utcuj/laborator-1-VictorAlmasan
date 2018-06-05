package league.reporting;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;

import league.project.model.DBConnectionPool;
import league.project.util.AlertDialog;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

public class TeamReports {

	public static void teamStandings(String viewName) {
		try (Connection co = DBConnectionPool.getInstance().getConnection()) {

			StyleBuilder boldStyle = stl.style().bold();
			StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
			StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point())
					.setBackgroundColor(new Color(0, 102, 0)).setForegroundColor(Color.white);

			StyleBuilder titleStyle = stl.style(boldCenteredStyle).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFontSize(15);

			// definire coloane

			TextColumnBuilder<Integer> points = col.column("Points", "points", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<String> name = col.column("Team", "team_name", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Integer> matches = col.column("Matches Played", "matches_played", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Integer> draws = col.column("Draws", "draws", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Integer> wins = col.column("Wins", "wins", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Integer> losses = col.column("Losses", "losses", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);

			// raport nou
			JasperReportBuilder report = DynamicReports.report();
			// report.setTemplate(Templates.reportTemplate);
			report.setColumnTitleStyle(columnTitleStyle);

			// colorare randuri pare/impare
			report.setDetailEvenRowStyle(stl.simpleStyle().setBackgroundColor(new Color(144,238,144)));
			report.setDetailOddRowStyle(stl.simpleStyle().setBackgroundColor(new Color(142,255,195)));
			report.setHighlightDetailEvenRows(true);
			report.setHighlightDetailOddRows(true);

			report.title(cmp.horizontalList()
					.add(cmp.image("images/teams.jpg").setFixedDimension(150, 120),
							cmp.text("Team Standings ("+header(viewName)+")").setStyle(titleStyle)
									.setHorizontalAlignment(HorizontalAlignment.RIGHT))
					.newRow().add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)));

			// adaugare coloane
			report.columns(points, name, matches, draws, wins, losses);
			// report.groupBy(nume);

			// afisare nr paginii in josul paginii
			report.pageFooter(Components.pageXofY());
			// colectarea datelor din baza de date
			report.setDataSource("SELECT points, team_name, matches_played, draws, wins, losses FROM "+ viewName+" ORDER BY points DESC", co);

			try {
				// afisare raport
				report.show(false);
			} catch (DRException e) {
				AlertDialog.showError("Internal Error", "Generating report failed because: "+e.getMessage());
			}

		}catch (Exception e) {
			AlertDialog.showError("System Error (Reporting)",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
		}
	}

	private static String header(String viewName){
		return "teamsOverall".equalsIgnoreCase(viewName) ? "Overall" : "teamsHome".equalsIgnoreCase(viewName) ? "Home" : "Away";
	}

}
