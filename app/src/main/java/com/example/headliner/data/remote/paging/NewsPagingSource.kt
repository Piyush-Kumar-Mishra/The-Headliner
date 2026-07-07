package com.example.headliner.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.NewsResult
import com.example.headliner.domain.repository.NewsRepository

class NewsPagingSource(
    private val repository: NewsRepository,
    private val category: String,
    private val country: String,
    private val language: String
) : PagingSource<Int, Article>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return when (
            val result = repository.getTopHeadlines(
                category = category,
                country = country,
                language = language,
                max = 30,
                page = page
            )
        ) {
            is NewsResult.Success -> LoadResult.Page(
                data = result.data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.data.isEmpty()) null else page + 1
            )
            is NewsResult.Error -> LoadResult.Error(result.throwable ?: IllegalStateException(result.message))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
