output=$(pm2 start app.mjs 2>&1)

if [ "$?" -ne 0 ]; then
 echo "An error occurred: $output"
  exit 1
fi

if [[ "$output" == *"error"* ]]; then
  echo "An error occurred: $output"
  exit 1
fi

echo "Script ran successfully: $output"