package com.vakifbank.WatchWise.ui.fragment.seemore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.base.BaseFragment
import com.vakifbank.WatchWise.databinding.FragmentSeeMoreBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.ui.adapter.SearchMovieAdapter
import dagger.hilt.android.AndroidEntryPoint

enum class MovieCategory {
    POPULAR, TOP_RATED, UPCOMING
}

@AndroidEntryPoint
class SeeMoreFragment : BaseFragment() {

    private var _binding: FragmentSeeMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SeeMoreViewModel by viewModels()

    private lateinit var searchMovieAdapter: SearchMovieAdapter
    private val movieList = mutableListOf<Movie>()

    private var categoryType: MovieCategory = MovieCategory.POPULAR
    private var categoryTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            categoryType = when (bundle.getString("category_type")) {
                "popular" -> MovieCategory.POPULAR
                "top_rated" -> MovieCategory.TOP_RATED
                "upcoming" -> MovieCategory.UPCOMING
                else -> MovieCategory.POPULAR
            }
            categoryTitle = bundle.getString("category_title") ?: "Filmler"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeeMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupFavoriteButton(binding.favoriteFab)
        setupObservers()

        viewModel.setCategoryType(categoryType)
        viewModel.loadMovies(1)
    }

    private fun setupObservers() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieList.clear()
            movieList.addAll(movies)
            searchMovieAdapter.notifyDataSetChanged()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        binding.categoryTitleTextView.text = categoryTitle
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        searchMovieAdapter = SearchMovieAdapter(movieList) { movie ->
            onMovieClick(movie)
        }

        binding.moviesRecyclerView.apply {
            adapter = searchMovieAdapter
            layoutManager = LinearLayoutManager(context)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Sayfanın sonuna yaklaştığında yeni sayfa yükle
                    if (!viewModel.isLoading.value!! && viewModel.hasMorePages.value!!) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            })
        }
    }

    private fun onMovieClick(movie: Movie) {
        if (movie.id == null || movie.id == -1) {
            return
        }

        viewModel.loadMovieDetailsForNavigation(movie.id) { movieDetail ->
            val bundle = Bundle().apply {
                putParcelable("movie_detail_data", movieDetail)
            }
            findNavController().navigate(R.id.action_seeMoreFragment_to_movieDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}