


const ModelInfoTab: React.FC<API.ModelConfig> = (model) => {
  return <>
    {JSON.stringify(model, null, 4)}
  </>

}

export default ModelInfoTab;
