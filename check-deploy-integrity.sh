#!/usr/bin/env bash
set -euo pipefail
trap "echo 'error: Script failed: see failed command above'" ERR

git_hash_of_previous_iteration=""
for i in `seq 1 50`; do
	res=$(curl -sw "%{http_code}" https://dev-cpc.devappdirect.me/info) # stolen from http://stackoverflow.com/a/37072904/26605
	http_code="${res:${#res}-3}"
	body="${res:0:${#res}-3}"

    if [ "$http_code" != "200" ]; then
        echo ERROR: Expected a 200, but got a $http_code
        exit 1
    fi

    git_hash_of_current_iteration=$(echo $body | python -c "import sys, json; print json.load(sys.stdin)['git']['commit']['id']")

    if [[ ! $git_hash_of_previous_iteration ]]; then
        git_hash_of_previous_iteration=$git_hash_of_current_iteration
    fi

    if [[ "$git_hash_of_previous_iteration" != "$git_hash_of_current_iteration" ]]; then
        echo ERROR: Git hashes are not all the same! Previous iteration: $git_hash_of_previous_iteration \| Current iteration: $git_hash_of_current_iteration
        exit 1
    fi
done

echo SUCCESS! All pods are reporting the same git hash \(#$git_hash_of_previous_iteration\)!
