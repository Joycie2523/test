package joyceit2d;

import java.sql.*;
import java.util.Scanner;

public class LoanReport {

    public void generateReport() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.println("\nREPORT MENU:");
        System.out.println("1. Individual Report ");
        System.out.println("2. General Report ");

        System.out.print("Enter your choice : ");
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter 1 or 2.");
            sc.next();
            System.out.print("Enter your choice : ");
        }
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                generateIndividualReport(conf);
                break;
            case 2:
                generateGeneralReport(conf);
                break;
            case 3:
                loanapplication lp = new loanapplication();
                lp.viewLoanApplications();
                System.out.println("Enter ID: ");
                int id = sc.nextInt();
                viewInd(id);
                break;
            default:
                System.out.println("Invalid choice. Please enter 1 or 2.");
                break;
        }
    }
    
      public void viewInd(int id){
  
      config conf = new config();
      String qry = "SELECT * FROM tbl_loan_application JOIN tbl_applicant ON tbl_applicant.a_id = tbl_loan_application.a_id WHERE tbl_loan_application.a_id = '"+id+"'";
      
        String[] headers = {"ID", "First name",  "Loan Date", "Amount"};
        String[] columns = {"tbl_loan_application.a_id", "a_fname", "loan_date", "repayable_amount"};
        
        conf.viewRecords(qry, headers, columns);
      
  }

    private void generateIndividualReport(config conf) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Applicant ID for the report: ");
        int applicantId = sc.nextInt();

        if (!isApplicantExist(conf, applicantId)) {
            System.out.println("Applicant ID does not exist. Please try again.");
            return;
        }

        String query = "SELECT tbl_loan_application.loan_id, tbl_loan_application.loan_type, tbl_loan_application.loan_amount, " +
                       "tbl_loan_application.loan_term, tbl_loan_application.interest_rate, tbl_loan_application.repayable_amount, " +
                       "tbl_loan_application.loan_date, tbl_loan_application.loan_status " +
                       "FROM tbl_loan_application " +
                       "LEFT JOIN tbl_applicant ON tbl_loan_application.a_id = tbl_applicant.a_id " +
                       "WHERE tbl_loan_application.a_id = ?";

        ResultSet resultSet = conf.getData(query, applicantId);

        String applicantNameQuery = "SELECT a_fname, a_lname FROM tbl_applicant WHERE a_id = ?";
        ResultSet nameResultSet = conf.getData(applicantNameQuery, applicantId);
        String firstName = "";
        String lastName = "";
        try {
            if (nameResultSet.next()) {
                firstName = nameResultSet.getString("a_fname");
                lastName = nameResultSet.getString("a_lname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- Individual Loan Report ---");
        System.out.println("Applicant Name: " + firstName + " " + lastName);
        System.out.println("-----------------------------------");

        // Print table headers with larger widths to accommodate longer values
        System.out.printf("%-10s%-20s%-15s%-15s%-18s%-20s%-20s%-15s\n", 
                "Loan ID", "Loan Type", "Loan Amount", "Loan Term", "Interest Rate", 
                "Repayable Amount", "Loan Date", "Loan Status");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        printReport(resultSet);
    }

    private boolean isApplicantExist(config conf, int applicantId) {
        String checkQuery = "SELECT COUNT(*) FROM tbl_applicant WHERE a_id = ?";
        ResultSet rs = conf.getData(checkQuery, applicantId);
        try {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void generateGeneralReport(config conf) {
        String query = "SELECT tbl_applicant.a_id, tbl_applicant.a_fname, tbl_applicant.a_lname, " +
                       "tbl_loan_application.loan_amount, tbl_loan_application.repayable_amount " +
                       "FROM tbl_loan_application " +
                       "LEFT JOIN tbl_applicant ON tbl_loan_application.a_id = tbl_applicant.a_id";

        ResultSet resultSet = conf.getData(query);

        System.out.println("\n--- General Loan Report ---");
        // Print table headers with larger widths to accommodate longer values
        System.out.printf("%-15s%-20s%-20s%-15s%-15s\n", 
                "Applicant ID", "First Name", "Last Name", "Loan Amount", "Repayable Amount");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        printReport(resultSet);
    }

    private void printReport(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    if (value == null) value = "N/A";
                 
                    System.out.print(String.format("%-20s", value)); 
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
