import { useEffect, useState } from 'react';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { TreeView } from '@mui/x-tree-view/TreeView';
import { TreeItem } from '@mui/x-tree-view/TreeItem';
import { DeltaStreamService } from './services/DeltaStreamService';
import { CircularProgress, Typography } from '@mui/material';
import IStore from './types/Store.type';
import IQuery from './types/Query.type';

const DeltaStreamView = () => {

  const [state, setState] = useState({
    loading: false,
    orgData: { },
  });

  const fetchData = async () => {
    setState({ ...state, loading: true });
    let response = await DeltaStreamService.getOrgData();
    setState({
      ...state,
      loading: false,
      orgData: response.data
    })
  }

  useEffect(() => {
    fetchData();
  }, []);


  let { loading, orgData } = state;

  return (
    <>
    <Typography variant="h5" component="h5" gutterBottom margin={'10px'}>DeltaStream store</Typography>
      {loading ? <CircularProgress /> : <>
        <TreeView
          aria-label="file system navigator"
          defaultCollapseIcon={<ExpandMoreIcon />}
          defaultExpandIcon={<ChevronRightIcon />}
          sx={{ flexGrow: 1 }}
        >
          <TreeItem key='storeKey' nodeId='storeNode' label='Stores'>
          {orgData.hasOwnProperty("stores") && orgData['stores'].map((store:IStore) => (
           
              <TreeItem key={store.name} nodeId={store.name} label={`${store.name}[${store.type}]`} >
                {store.hasOwnProperty("topicNames") && store["topicNames"] !== null &&
                  store["topicNames"].map((topic: string, index: string) => <TreeItem key={topic + index} nodeId={topic} label={`${topic}`} />)
                }
              </TreeItem>
          ))}
          </TreeItem>
          
          <TreeItem key='dbKey' nodeId='databaseNode' label='Databases'>
          {orgData.hasOwnProperty("databases") && orgData["databases"] !== null && orgData['databases'].map((database) => (
            <TreeItem key={database.name} nodeId={database.name} label={`${database.name}`} >
              {database.hasOwnProperty("schemas") &&
                database["schemas"].map((schema, index) => <TreeItem key={schema.name + index} nodeId={schema.name} label={`${schema.name}`} > 
                {schema.hasOwnProperty("relations") &&
                  schema["relations"].map((relation, index) => <TreeItem key={relation.name + index} nodeId={relation.name} label={`${relation.name}`} />)
                }                
                </TreeItem>)
              }
            </TreeItem>
          ))}
          </TreeItem>

           <TreeItem key='queryKey' nodeId='queryNode' label='Queries'>
            {orgData.hasOwnProperty("queries") && orgData['queries'].map((query:IQuery, index) => (
                <TreeItem key={index} nodeId={index} label={`${query.dsql}`} sx={{
                    "& .MuiTreeItem-label": {
                               fontSize: ".7rem",
                               lineHeight: 2.5
                            }
                    }}/>)
            )}
            </TreeItem>

        </TreeView>
      </>

      }
    </>
  );
}


export default DeltaStreamView;