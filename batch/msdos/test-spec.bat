md "%HOMEPATH%\_delete_content\"
pushd %~dp0\..\..
call lein test :only pl.tomaszgigiel.file-tool
pause
popd
