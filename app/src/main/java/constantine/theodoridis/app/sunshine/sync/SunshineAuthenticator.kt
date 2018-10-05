/*
 *  Copyright (C) 2018 Constantine Theodoridis
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package constantine.theodoridis.app.sunshine.sync

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.NetworkErrorException
import android.content.Context
import android.os.Bundle

class SunshineAuthenticator(context: Context) : AbstractAccountAuthenticator(context) {
	override fun editProperties(r: AccountAuthenticatorResponse, s: String): Bundle {
		throw UnsupportedOperationException()
	}

	@Throws(NetworkErrorException::class)
	override fun addAccount(r: AccountAuthenticatorResponse, s: String, s2: String, strings: Array<String>, bundle: Bundle): Bundle? {
		return null
	}

	@Throws(NetworkErrorException::class)
	override fun confirmCredentials(r: AccountAuthenticatorResponse, account: Account, bundle: Bundle): Bundle? {
		return null
	}

	@Throws(NetworkErrorException::class)
	override fun getAuthToken(r: AccountAuthenticatorResponse, account: Account, s: String, bundle: Bundle): Bundle {
		throw UnsupportedOperationException()
	}

	override fun getAuthTokenLabel(s: String): String {
		throw UnsupportedOperationException()
	}

	@Throws(NetworkErrorException::class)
	override fun updateCredentials(r: AccountAuthenticatorResponse, account: Account, s: String, bundle: Bundle): Bundle {
		throw UnsupportedOperationException()
	}

	@Throws(NetworkErrorException::class)
	override fun hasFeatures(r: AccountAuthenticatorResponse, account: Account, strings: Array<String>): Bundle {
		throw UnsupportedOperationException()
	}
}
