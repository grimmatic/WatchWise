package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentFavoriteBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.usecase.GetMovieDetailsUseCase
import com.vakifbank.WatchWise.ui.fragment.favorite.FavoriteViewModel
import com.vakifbank.WatchWise.ui.adapter.SearchMovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()

    @Inject
    lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    private lateinit var auth: FirebaseAuth
    private lateinit var favoriteMovieAdapter: SearchMovieAdapter
    private val favoriteMovieList = mutableListOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        setupRecyclerView()
        setupObservers()
        checkAuthAndLoadFavorites()
    }

    private fun setupObservers() {
        viewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
            favoriteMovieList.clear()
            favoriteMovieList.addAll(movies)
            favoriteMovieAdapter.notifyDataSetChanged()

            binding.favoriteMoviesRecylerView.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                showEmptyState("Henüz favori filminiz yok.\nFilmleri favorilerinize ekleyerek burada görebilirsiniz.")
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showEmptyState("Favori filmler yüklenirken bir hata oluştu.")
                Toast.makeText(requireContext(), "Favori filmler yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        favoriteMovieAdapter = SearchMovieAdapter(favoriteMovieList) { movie ->
            onMovieClick(movie)
        }

        binding.favoriteMoviesRecylerView.apply {
            adapter = favoriteMovieAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun checkAuthAndLoadFavorites() {
        if (auth.currentUser != null) {
            viewModel.loadFavoriteMovies()
        } else {
            showEmptyState("Favori filmlerinizi görmek için giriş yapmalısınız.")
        }
    }

    private fun showEmptyState(message: String) {
        binding.favoriteMoviesRecylerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.emptyStateText.text = message
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.favoriteMoviesRecylerView.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun onMovieClick(movie: Movie) {
        if (movie.id == null || movie.id == -1) {
            return
        }

        val call = getMovieDetailsUseCase(movie.id)
        call.enqueue(object : Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                if (response.isSuccessful) {
                    response.body()?.let { movieDetail ->
                        val bundle = Bundle().apply {
                            putParcelable("movie_detail_data", movieDetail)
                        }
                        findNavController().navigate(R.id.action_favoriteFragment_to_movieDetailFragment, bundle)
                    }
                }
            }

            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                Toast.makeText(requireContext(), "Film detayları yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            viewModel.loadFavoriteMovies()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}