package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vakifbank.WatchWise.data.repository.MoviesRepository
import com.vakifbank.WatchWise.databinding.FragmentMovieListBinding
import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.ui.adapter.MovieAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private lateinit var movieAdapter: MovieAdapter
    private val movieList = mutableListOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //MoviesRepository.getPopularMovies(1)
        setUpRecyclerView()
        loadPopularMovies()
    }
    fun setUpRecyclerView(){
        movieAdapter= MovieAdapter(movieList){ movie ->
            onMovieClick(movie)
        }
        binding.populerRecyclerView.apply {
            adapter=movieAdapter
            layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

    }

    fun loadPopularMovies(){
        val call= MoviesRepository.getPopularMovies(1)

        call.enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(
                call: Call<GetMoviesResponse>,
                response: Response<GetMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    moviesResponse?.movies?.let { movies ->
                        movieList.clear()
                        movieList.addAll(movies)
                        movieAdapter.notifyDataSetChanged()

                    }
                } else {
                    println("cevap dönmedi - response: $response")


                }
            }

            override fun onFailure(call: Call<GetMoviesResponse?>, t: Throwable) {

            }

        })

    }
    private fun onMovieClick(movie: Movie) {
        // Film detay sayfasına git
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}