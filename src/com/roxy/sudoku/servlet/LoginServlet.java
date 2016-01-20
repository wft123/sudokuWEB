package com.roxy.sudoku.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roxy.sudoku.SudokuLogic;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/logic")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SudokuLogic logic = new SudokuLogic();
		request.setAttribute("logic", logic);
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}


}
