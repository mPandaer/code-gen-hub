

const FileInfoTab: React.FC<API.FileConfig> = (fileConfig) => {
  return <>
    {
      JSON.stringify(fileConfig, null, 4)
    }
  </>

}

export default FileInfoTab;
