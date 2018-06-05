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

public class PlayerReports {

	public static void playerStandingsByPosition(String posCode) {
		try (Connection co = DBConnectionPool.getInstance().getConnection()) {

			StyleBuilder boldStyle = stl.style().bold();
			StyleBuilder coloredStyle = stl.style(boldStyle).setForegroundColor(new Color(0, 0, 153));
			StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
			StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point())
					.setBackgroundColor(new Color(160,82,45)).setForegroundColor(Color.white);

			StyleBuilder titleStyle = stl.style(boldCenteredStyle).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFontSize(15);

			// definire coloane

			TextColumnBuilder<String> player = col.column("Player", "player_name", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<String> team = col.column("Team", "team_name", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Integer> goals = col.column("Goals", "goals", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);

			// raport nou
			JasperReportBuilder report = DynamicReports.report();
			// report.setTemplate(Templates.reportTemplate);
			report.setColumnTitleStyle(columnTitleStyle);

			// colorare randuri pare/impare
			report.setDetailEvenRowStyle(stl.simpleStyle().setBackgroundColor(new Color(222,184,135)));
			report.setDetailOddRowStyle(stl.simpleStyle().setBackgroundColor(new Color(255,239,213)));
			report.setHighlightDetailEvenRows(true);
			report.setHighlightDetailOddRows(true);

			report.title(cmp.horizontalList()
					.add(cmp.image("images/logo.png").setFixedDimension(90, 90),
							cmp.text("Scorer Standings By Player Positions").setStyle(titleStyle)
									.setHorizontalAlignment(HorizontalAlignment.RIGHT))
					.newRow().add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)));

			// adaugare coloane
			report.columns(player, team, goals);
			// report.groupBy(nume);

			// afisare nr paginii in josul paginii
			report.pageFooter(Components.pageXofY());
			// colectarea datelor din baza de date
			report.setDataSource("SELECT team_name, player_name, goals FROM scorerStandings WHERE player_position_code='"+posCode+"' ORDER BY goals DESC", co);

			try {
				// afisare raport
				report.show(false);
			} catch (DRException e) {
				e.printStackTrace();
				AlertDialog.showError("Internal Error", "The report could not be generated!");
			}

		} catch (Exception e) {
			AlertDialog.showError("System Error (Reporting)",
					"There was an error (" + e + "). Contact the system administrator if this problem persists!");
		}
	}

	public static void playerStandings() {
		try (Connection co = DBConnectionPool.getInstance().getConnection()) {

			StyleBuilder boldStyle = stl.style().bold();
			StyleBuilder coloredStyle = stl.style(boldStyle).setForegroundColor(new Color(255,0,0));
			StyleBuilder boldCenteredStyle = stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
			StyleBuilder columnTitleStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point())
					.setBackgroundColor(new Color(75,0,130)).setForegroundColor(Color.white);

			StyleBuilder titleStyle = stl.style(boldCenteredStyle).setVerticalAlignment(VerticalAlignment.MIDDLE)
					.setFontSize(15);

			// definire coloane

			TextColumnBuilder<String> pos = col.column("Player Position", "player_position", type.stringType()).setStyle(coloredStyle);

			TextColumnBuilder<String> player = col.column("Player", "player_name", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<String> team = col.column("Team", "team_name", type.stringType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
			TextColumnBuilder<Integer> goals = col.column("Goals", "goals", type.integerType())
					.setHorizontalAlignment(HorizontalAlignment.CENTER);

			// raport nou
			JasperReportBuilder report = DynamicReports.report();
			// report.setTemplate(Templates.reportTemplate);
			report.setColumnTitleStyle(columnTitleStyle);

			// colorare randuri pare/impare
			report.setDetailEvenRowStyle(stl.simpleStyle().setBackgroundColor(new Color(216,191,216)));
			report.setDetailOddRowStyle(stl.simpleStyle().setBackgroundColor(new Color(250,235,215)));
			report.setHighlightDetailEvenRows(true);
			report.setHighlightDetailOddRows(true);

			report.title(cmp.horizontalList()
					.add(cmp.image("images/logo.png").setFixedDimension(90, 90),
							cmp.text("Scorer Standings").setStyle(titleStyle)
									.setHorizontalAlignment(HorizontalAlignment.RIGHT))
					.newRow().add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)));

			// adaugare coloane
			report.columns(pos, player, team, goals);
			report.groupBy(pos);

			// afisare nr paginii in josul paginii
			report.pageFooter(Components.pageXofY());
			// colectarea datelor din baza de date
			report.setDataSource("SELECT player_position, team_name, player_name, goals FROM scorerStandings ORDER BY goals DESC", co);

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


}
