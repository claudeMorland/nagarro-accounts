package com.nagarro.accounts.services;

import static com.nagarro.accounts.common.ApplicationConstants.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import com.nagarro.accounts.persistence.entity.Statement;

/**
 * Accounts and statement business service
 * 
 * @author claud
 *
 */
@Service
public class AccountsService {

	//COLUMN_ACCOUNT_NUMBER
	private static final String COL_ACCOUNT_NUMBER = "account_number";
	//COLUMN_ACCOUNT_TYPE
	private static final String COL_ACCOUNT_TYPE = "account_type";
	//COLUMN_ACCOUNT_ID
	private static final String COL_ACCOUNT_ID = "account_id";
	//COLUMN_AMOUNT
	private static final String COL_AMOUNT = "amount";
	//COLUMN_DATEFIELD
	private static final String COL_DATEFIELD = "datefield";
	//BDD DATE FORMATTER
	private static final DateTimeFormatter FORMATTER_BDD = DateTimeFormatter.ofPattern(DATE_FORMAT_BDD);

	/**
	 * months back statement number
	 */
	@Value("${accounts.monthsback.value:3}")
	public Integer monthsBackValue;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Get Statement by range of date
	 * @param accountId
	 * @param dateStart
	 * @param dateEnd
	 * @return list of statement
	 */
	@Secured({ ADMIN_ROLE })
	public List<Statement> getStatementByDateRange(Integer accountId, LocalDate dateStart, LocalDate dateEnd) {
		List<Statement> statements = new ArrayList<>();

		this.jdbcTemplate.query(
				"SELECT s.id, s.account_id, s.datefield, s.amount, a.account_type, a.account_number"
				+ " FROM statement s, account a where s.account_id=? and s.account_id=a.ID",
				new RowCallbackHandler() {
					public void processRow(ResultSet resultSet) throws SQLException {
						while (resultSet.next()) {
							String dateField = resultSet.getString(COL_DATEFIELD);
							// process it
							LocalDate dateFieldDate = LocalDate.parse(dateField, FORMATTER_BDD);

							// the dateField is in the range (> or = to dateStart and < or = to dateEnd)
							if ((dateStart.equals(dateFieldDate) || dateFieldDate.isAfter(dateStart))
									&& (dateEnd.equals(dateFieldDate) || dateFieldDate.isBefore(dateEnd))) {
								statements.add(new Statement(resultSet.getInt("id"), resultSet.getInt(COL_ACCOUNT_ID),
										resultSet.getString(COL_DATEFIELD), resultSet.getString(COL_AMOUNT),
										resultSet.getString(COL_ACCOUNT_TYPE), resultSet.getString(COL_ACCOUNT_NUMBER)));
							}
						}
					}
				}, accountId);

		return statements;
	}

	/**
	 * Get Statement by range of amount
	 * @param accountId
	 * @param amountStart
	 * @param amountEnd
	 * @return
	 */
	@Secured({ ADMIN_ROLE })
	public List<Statement> getStatementByAmountRange(Integer accountId, Integer amountStart, Integer amountEnd) {
		List<Statement> statements = new ArrayList<>();

		this.jdbcTemplate.query(
				"SELECT s.id, s.account_id, s.datefield, s.amount, a.account_type, a.account_number FROM statement s, account a where s.account_id=? and s.account_id=a.ID",
				new RowCallbackHandler() {
					public void processRow(ResultSet resultSet) throws SQLException {
						while (resultSet.next()) {
							String amount = resultSet.getString(COL_AMOUNT);
							// process it

							Double amountInt = Double.parseDouble(amount);

							Double amountStartInt = Double.valueOf(amountStart);
							Double amountEndInt = Double.valueOf(amountEnd);

							// the amount is in the range (amountStart <= amount <= amountEnd)
							if (amountInt >= amountStartInt && amountInt <= amountEndInt) {
								statements.add(new Statement(resultSet.getInt("id"), resultSet.getInt(COL_ACCOUNT_ID),
										resultSet.getString(COL_DATEFIELD), resultSet.getString(COL_AMOUNT),
										resultSet.getString(COL_ACCOUNT_TYPE), resultSet.getString(COL_ACCOUNT_NUMBER)));
							}
						}
					}
				}, accountId);

		return statements;
	}

	/**
	 * List of statements on a accountId during the last X Months
	 * 
	 * @param accountId
	 * @return list of statements on a last number of months
	 */
	@Secured({ USER_ROLE, ADMIN_ROLE })
	public List<Statement> getStatementLastMonths(Integer accountId) {
		// lastMonth is the x last month : start = getStatementByAmountRange and end =
		// today
		LocalDate dateNow = LocalDate.now();

		// end = today
		LocalDate dateEnd = dateNow;

		// start = today - x month
		LocalDate dateStart = dateNow.minusMonths(monthsBackValue);

		return getStatementByDateRange(accountId, dateStart, dateEnd);
	}

}
