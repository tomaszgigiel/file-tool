md "%HOMEPATH%\_delete_content\"
pushd %~dp0\..\..
if not exist ".\target\uberjar\file-tool-uberjar.jar" (
  rmdir /s /q target
  call lein do clean, uberjar
)
call java -cp .\target\uberjar\file-tool-uberjar.jar pl.tomaszgigiel.file-tool.core
pause
popd
