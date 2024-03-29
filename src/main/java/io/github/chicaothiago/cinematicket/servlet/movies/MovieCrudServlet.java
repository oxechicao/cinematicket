package io.github.chicaothiago.cinematicket.servlet.movies;

import io.github.chicaothiago.cinematicket.bean.CinemaBean;
import io.github.chicaothiago.cinematicket.bean.MovieBean;
import io.github.chicaothiago.cinematicket.dao.CinemaDao;
import io.github.chicaothiago.cinematicket.dao.MovieDao;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "MoviesCrud", urlPatterns = {"/movies/crud", "/movies/crud/"})
public class MovieCrudServlet extends HttpServlet {
    MovieDao movieDao = new MovieDao();
    CinemaDao cinemaDao = new CinemaDao();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        ArrayList<CinemaBean> cinemas = new ArrayList<>();
        try {
            cinemas = cinemaDao.selectAll();
            req.setAttribute("cinemas", cinemas);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (cinemas.size() == 0) {
            resp.sendRedirect(req.getContextPath() + "/cinema");
        }

        MovieBean movie = new MovieBean();
        if (req.getParameter("id") != null) {
            req.setAttribute("id", req.getParameter("id"));

            try {
                movie = movieDao.getMovie(Integer.parseInt(req.getParameter("id")));
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        req.setAttribute("movie", movie);

        req.getRequestDispatcher("/WEB-INF/views/movies/form.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        if (req.getParameter("delete_id") != null) {
            this.doDelete(req, resp);
            return;
        }

        if (req.getParameter("id") != null) {
            this.doPut(req, resp);
            return;
        }

        if (req.getParameterValues("cinemas[]") == null) {
            req.getRequestDispatcher(req.getHeader("Referer")).forward(req, resp);
        }

        this.movieDao.insertMovie(req);

        resp.sendRedirect(req.getContextPath() + "/movies");
    }

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            movieDao.update(req);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/movies");
    }

    @Override protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        movieDao.deleteMovie(Integer.parseInt(req.getParameter("delete_id")));
        resp.sendRedirect(req.getContextPath() + "/movies");
    }
}
