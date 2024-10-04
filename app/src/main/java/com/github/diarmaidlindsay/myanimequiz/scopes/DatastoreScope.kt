package com.github.diarmaidlindsay.myanimequiz.scopes

import kotlinx.coroutines.CoroutineScope

/**
 * A non-lifecycle aware scope for datastore operations only
 */
interface DatastoreScope : CoroutineScope